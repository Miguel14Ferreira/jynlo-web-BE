package com.example.desporto24.service;

import com.example.desporto24.model.*;
import com.example.desporto24.repository.*;
import com.example.desporto24.service.interfaces.ChatService;
import com.example.desporto24.storage.MediaStorage;
import org.springframework.web.multipart.MultipartFile;
import java.net.URI;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * New private group threads; they NEVER reuse or copy messages from a private
 * direct chat. New members only see messages created after their joining time.
 */
@Service @RequiredArgsConstructor
public class PrivateGroupService {
    private static final int MAX_MEMBERS=30;
    private final PrivateGroupRepository groups;
    private final PrivateGroupMemberRepository members;
    private final PrivateGroupMessageRepository messages;
    private final ProfileRepository profiles;
    private final ChatService chatService;
    private final MediaStorage mediaStorage;
    private final ApplicationEventPublisher events;
    @org.springframework.beans.factory.annotation.Autowired(required=false)
    private com.example.desporto24.storage.PrivateChatFileStorage privateFiles;

    public record MemberView(String username,String displayName,String profilePicUrl,boolean admin) {}
    public record GroupView(Long id,String title,String creatorUsername,List<MemberView> members,
                            Instant createdAt,String photoUrl) {}
    public record MessageView(Long id,Long groupId,String sender,String text,Instant createdAt,
                              Long replyToId,Instant editedAt,boolean deleted,
                              List<PrivateChatAttachmentBatch.PublicMetadata> attachments) {
        /** Text-only message (older callers and tests). */
        public MessageView(Long id,Long groupId,String sender,String text,Instant createdAt){
            this(id,groupId,sender,text,createdAt,null,null,false,List.of());
        }
    }
    public record CreateRequest(String title,List<String> usernames) {}
    public record AddRequest(List<String> usernames) {}
    public record RenameRequest(String title) {}
    public record SendRequest(String text,Long replyToId) {
        public SendRequest(String text){ this(text,null); }
    }
    public record EditRequest(String text) {}
    public record AttachmentContent(byte[] content,String mimeType,String filename) {}
    public record GroupEvent(Long groupId,Set<String> usernames,MessageView message) {}

    @Transactional(readOnly=true)
    public List<GroupView> list(String actor) {
        Profile self=actor(actor);
        List<GroupView> result=new ArrayList<>();
        for(var member:members.findByUsernameIgnoreCaseOrderByGroupIdDesc(self.getUsername())){
            groups.findById(member.getGroupId()).ifPresent(g->result.add(view(g)));
        }
        return result;
    }

    @Transactional(readOnly=true)
    public GroupView get(String actor,Long id) {
        return view(authorized(actor,id));
    }

    @Transactional
    public GroupView create(String actor,CreateRequest request) {
        Profile self=actor(actor);
        String title=validTitle(request==null?null:request.title());
        List<Profile> participants=invitees(self,request==null?null:request.usernames());
        PrivateGroup g=new PrivateGroup();
        g.setTitle(title);
        g.setCreatorUsername(self.getUsername());
        g.setCreatedAt(Instant.now());
        g=groups.saveAndFlush(g);
        addMember(g.getId(),self.getUsername(),g.getCreatedAt());
        for(Profile p:participants)addMember(g.getId(),p.getUsername(),g.getCreatedAt());
        GroupView created=view(g);
        events.publishEvent(new GroupEvent(g.getId(),usernames(created),null));
        return created;
    }

    @Transactional
    public GroupView add(String actor,Long id,AddRequest request) {
        PrivateGroup group=authorized(actor,id);
        if(!group.getCreatorUsername().equalsIgnoreCase(actor))
            throw new AccessDeniedException("Only the group creator can add participants");
        Profile self=actor(actor);
        List<Profile> add=invitees(self,request==null?null:request.usernames());
        if(members.countByGroupId(id)+add.size()>MAX_MEMBERS)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Group is full");
        for(Profile p:add){
            if(members.existsByGroupIdAndUsernameIgnoreCase(id,p.getUsername()))
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Already a group member");
        }
        Instant when=Instant.now();
        for(Profile p:add)addMember(id,p.getUsername(),when);
        GroupView current=view(group);
        events.publishEvent(new GroupEvent(id,usernames(current),null));
        return current;
    }


    /** Only the creator can rename a group. No direct-chat data is touched. */
    @Transactional
    public GroupView rename(String actor, Long id, RenameRequest request) {
        PrivateGroup group=authorized(actor,id);
        if(!group.getCreatorUsername().equalsIgnoreCase(actor))
            throw new AccessDeniedException("Only the group creator can rename the group");
        group.setTitle(validTitle(request==null?null:request.title()));
        groups.save(group);
        GroupView updated=view(group);
        events.publishEvent(new GroupEvent(id,usernames(updated),null));
        return updated;
    }

    /** Creator can upload a validated photo through the same storage policy as community photos.
     * The group ID is used only inside a non-guessable object key; the membership
     * endpoint never returns photos or other group information to nonmembers.
     */
    @Transactional
    public GroupView updatePhoto(String actor, Long id, MultipartFile photo) {
        PrivateGroup group=authorized(actor,id);
        if(!group.getCreatorUsername().equalsIgnoreCase(actor))
            throw new AccessDeniedException("Only the group creator can change the photo");
        if(photo==null||photo.isEmpty()||photo.getSize()>5L*1024*1024)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Image must be 1-5 MB");
        URI stored;
        try {
            stored=mediaStorage.storeCommunityPhoto("private-groups/"+id+"/"+UUID.randomUUID(),photo);
        } catch(IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid image");
        }
        group.setPhotoUrl(stored.toString());
        groups.save(group);
        GroupView updated=view(group);
        events.publishEvent(new GroupEvent(id,usernames(updated),null));
        return updated;
    }

    /** Creator removes other members; ordinary members can only leave themselves.
     * The creator cannot leave without an ownership-transfer mechanism.
     * A removed member's real-time subscription receives only an invalidation.
     */
    @Transactional
    public GroupView removeMember(String actor, Long id, String targetUsername) {
        PrivateGroup group=authorized(actor,id);
        Profile self=actor(actor);
        if(targetUsername==null||targetUsername.isBlank()||targetUsername.length()>128)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid participant");
        PrivateGroupMember target=members.findByGroupIdAndUsernameIgnoreCase(id,targetUsername.trim())
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Participant not found"));
        boolean selfRemoval=target.getUsername().equalsIgnoreCase(self.getUsername());
        boolean creator=group.getCreatorUsername().equalsIgnoreCase(self.getUsername());
        if(!creator&&!selfRemoval)
            throw new AccessDeniedException("Only the group creator can remove others");
        if(target.getUsername().equalsIgnoreCase(group.getCreatorUsername()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Creator cannot leave without transferring ownership");
        members.delete(target);
        members.flush();
        GroupView updated=view(group);
        Set<String> audience=new HashSet<>(usernames(updated));
        audience.add(target.getUsername()); // Invalidate the removed user's inbox.
        events.publishEvent(new GroupEvent(id,Set.copyOf(audience),null));
        return updated;
    }

    @Transactional(readOnly=true)
    public List<MessageView> history(String actor,Long id,Long beforeId) {
        PrivateGroupMember membership=membership(actor,id);
        if(beforeId!=null&&beforeId<=0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        // at most 50 records; pagination uses keyset IDs.
        List<PrivateGroupMessage> saved=beforeId==null
            ? messages.findByGroupIdAndCreatedAtGreaterThanEqualOrderByIdDesc(
                id,membership.getJoinedAt(),PageRequest.of(0,50))
            : messages.findByGroupIdAndCreatedAtGreaterThanEqualAndIdLessThanOrderByIdDesc(
                id,membership.getJoinedAt(),beforeId,PageRequest.of(0,50));
        List<MessageView> result=saved.stream().map(this::messageView).collect(Collectors.toCollection(ArrayList::new));
        Collections.reverse(result);
        return result;
    }

    @Transactional
    public MessageView send(String actor,Long id,SendRequest request) {
        PrivateGroupMember sender=membership(actor,id);
        String body=request==null?null:request.text();
        if(body==null||body.isBlank()||body.trim().length()>2000)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Message must be 1–2000 characters");
        Long replyToId=request.replyToId();
        if(replyToId!=null)requireReplyTarget(id,sender,replyToId);
        PrivateGroupMessage m=new PrivateGroupMessage();
        m.setGroupId(id);
        m.setSenderUsername(sender.getUsername());
        m.setBody(body.trim());
        m.setReplyToId(replyToId);
        m.setCreatedAt(Instant.now());
        var saved=messages.saveAndFlush(m);
        MessageView result=messageView(saved);
        GroupView group=view(groups.findById(id).orElseThrow());
        events.publishEvent(new GroupEvent(id,usernames(group),result));
        return result;
    }

    /** Probe used by clients to show the attachment button; members only. */
    @Transactional(readOnly=true)
    public boolean attachmentsAvailable(String actor,Long id){
        membership(actor,id);
        return privateFiles!=null&&privateFiles.isConfigured();
    }

    /** Photos, videos and PDFs (up to PrivateChatAttachmentBatch.MAX_FILES), stored privately. */
    @Transactional
    public MessageView upload(String actor,Long id,List<MultipartFile> uploads,String caption,Long replyToId){
        PrivateGroupMember sender=membership(actor,id);
        if(privateFiles==null||!privateFiles.isConfigured())
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Armazenamento privado indisponível");
        String body=caption==null?"":caption.trim();
        if(body.length()>2000)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Legenda demasiado longa");
        if(replyToId!=null)requireReplyTarget(id,sender,replyToId);
        var batch=PrivateChatAttachmentBatch.store(privateFiles,uploads);
        var first=batch.first();
        try{
            PrivateGroupMessage m=new PrivateGroupMessage();
            m.setGroupId(id);
            m.setSenderUsername(sender.getUsername());
            m.setBody(body.isBlank()?"📎 "+first.name():body);
            m.setReplyToId(replyToId);
            m.setCreatedAt(Instant.now());
            m.setAttachmentKey(first.key());
            m.setAttachmentName(first.name());
            m.setAttachmentMimeType(first.mime());
            m.setAttachmentSize(first.size());
            m.setExtraAttachments(new ArrayList<>(batch.extras()));
            var saved=messages.saveAndFlush(m);
            MessageView result=messageView(saved);
            events.publishEvent(new GroupEvent(id,usernames(view(groups.findById(id).orElseThrow())),result));
            return result;
        }catch(RuntimeException failure){
            PrivateChatAttachmentBatch.cleanup(privateFiles,batch.files(),failure);
            throw failure;
        }
    }

    @Transactional(readOnly=true)
    public AttachmentContent download(String actor,Long id,Long messageId,int index){
        PrivateGroupMember member=membership(actor,id);
        if(privateFiles==null||!privateFiles.isConfigured())
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Armazenamento privado indisponível");
        if(messageId==null||messageId<=0||index<0||index>=PrivateChatAttachmentBatch.MAX_FILES)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Ficheiro não encontrado");
        PrivateGroupMessage m=messages.findById(messageId).orElseThrow(()->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"Ficheiro não encontrado"));
        // Same group, visible to this member (sent after they joined), not deleted.
        if(!id.equals(m.getGroupId())||m.getDeletedAt()!=null||m.getAttachmentKey()==null
                ||m.getCreatedAt().isBefore(member.getJoinedAt()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Ficheiro não encontrado");
        if(index==0)return new AttachmentContent(privateFiles.read(m.getAttachmentKey()),m.getAttachmentMimeType(),m.getAttachmentName());
        var extras=m.getExtraAttachments();
        if(extras==null||extras.size()<index)throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Ficheiro não encontrado");
        var extra=extras.get(index-1);
        if(extra==null||extra.getStorageKey()==null||extra.getStorageKey().isBlank())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Ficheiro não encontrado");
        return new AttachmentContent(privateFiles.read(extra.getStorageKey()),extra.getMimeType(),extra.getFilename());
    }

    /** Only the sender edits; the text is replaced and marked as edited. */
    @Transactional
    public MessageView edit(String actor,Long id,Long messageId,EditRequest request){
        PrivateGroupMember member=membership(actor,id);
        PrivateGroupMessage m=visibleMessage(id,member,messageId);
        if(!m.getSenderUsername().equalsIgnoreCase(member.getUsername()))
            throw new AccessDeniedException("Só quem enviou pode editar");
        String body=request==null||request.text()==null?"":request.text().trim();
        if(body.isEmpty()||body.length()>2000)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Message must be 1–2000 characters");
        com.example.desporto24.service.ChatPoll.reject(body);
        m.setBody(body);
        m.setEditedAt(Instant.now());
        var saved=messages.saveAndFlush(m);
        events.publishEvent(new GroupEvent(id,usernames(view(groups.findById(id).orElseThrow())),null));
        return messageView(saved);
    }

    /** The sender or the group creator deletes for everyone; files are removed. */
    @Transactional
    public void delete(String actor,Long id,Long messageId){
        PrivateGroupMember member=membership(actor,id);
        PrivateGroupMessage m=visibleMessage(id,member,messageId);
        PrivateGroup group=groups.findById(id).orElseThrow(()->new AccessDeniedException("No accessible group"));
        boolean own=m.getSenderUsername().equalsIgnoreCase(member.getUsername());
        boolean creator=member.getUsername().equalsIgnoreCase(group.getCreatorUsername());
        if(!own&&!creator)throw new AccessDeniedException("Só quem enviou ou quem criou o grupo pode apagar");
        List<String> keys=new ArrayList<>();
        if(m.getAttachmentKey()!=null)keys.add(m.getAttachmentKey());
        if(m.getExtraAttachments()!=null)m.getExtraAttachments().forEach(a->{if(a!=null&&a.getStorageKey()!=null)keys.add(a.getStorageKey());});
        m.setBody("");
        m.setDeletedAt(Instant.now());
        m.setAttachmentKey(null);m.setAttachmentName(null);m.setAttachmentMimeType(null);m.setAttachmentSize(null);
        m.setExtraAttachments(new ArrayList<>());
        messages.saveAndFlush(m);
        events.publishEvent(new GroupEvent(id,usernames(view(group)),null));
        if(privateFiles!=null)for(String key:keys){
            try{privateFiles.delete(key);}catch(RuntimeException ignored){ /* the message is already hidden */ }
        }
    }

    /** Text search within what this member can see (since they joined). */
    @Transactional(readOnly=true)
    public List<MessageView> search(String actor,Long id,String term){
        PrivateGroupMember member=membership(actor,id);
        String q=term==null?"":term.trim();
        if(q.length()<2||q.length()>80)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Pesquisa inválida");
        return messages.searchVisible(id,member.getJoinedAt(),q,PageRequest.of(0,50))
                .stream().map(this::messageView).toList();
    }

    private PrivateGroupMessage visibleMessage(Long id,PrivateGroupMember member,Long messageId){
        if(messageId==null||messageId<=0)throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        PrivateGroupMessage m=messages.findById(messageId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(!id.equals(m.getGroupId())||m.getCreatedAt().isBefore(member.getJoinedAt()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if(m.getDeletedAt()!=null)throw new ResponseStatusException(HttpStatus.CONFLICT,"Mensagem apagada");
        return m;
    }

    private void requireReplyTarget(Long id,PrivateGroupMember member,Long replyToId){
        var original=messages.findById(replyToId).orElseThrow(()->
            new ResponseStatusException(HttpStatus.BAD_REQUEST,"Mensagem original indisponível"));
        if(!id.equals(original.getGroupId())||original.getDeletedAt()!=null
                ||original.getCreatedAt().isBefore(member.getJoinedAt()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Resposta inválida");
    }

    private Set<String> usernames(GroupView view){
        return view.members().stream().map(MemberView::username).collect(Collectors.toUnmodifiableSet());
    }
    private MessageView messageView(PrivateGroupMessage m){
        boolean deleted=m.getDeletedAt()!=null;
        return new MessageView(m.getId(),m.getGroupId(),m.getSenderUsername(),deleted?"":m.getBody(),m.getCreatedAt(),
            m.getReplyToId(),m.getEditedAt(),deleted,
            deleted?List.of():PrivateChatAttachmentBatch.metadata(m.getAttachmentName(),m.getAttachmentMimeType(),
                m.getAttachmentSize(),m.getExtraAttachments()));
    }
    private GroupView view(PrivateGroup g){
        List<PrivateGroupMember> roster=members.findByGroupIdOrderByJoinedAtAsc(g.getId());
        List<MemberView> result=new ArrayList<>();
        for(var m:roster){
            Profile p=profiles.findUserByUsernameIgnoreCase(m.getUsername()).orElse(null);
            if(p==null)continue;
            String display=p.getNickname();
            if(display==null||display.isBlank())display=p.getFullName();
            if(display==null||display.isBlank())display=p.getUsername();
            result.add(new MemberView(p.getUsername(),display,p.getProfilePicUrl(),
                    p.getUsername().equalsIgnoreCase(g.getCreatorUsername())));
        }
        return new GroupView(g.getId(),g.getTitle(),g.getCreatorUsername(),List.copyOf(result),g.getCreatedAt(),g.getPhotoUrl());
    }
    private void addMember(Long id,String username,Instant when){
        PrivateGroupMember m=new PrivateGroupMember();
        m.setGroupId(id);m.setUsername(username);m.setJoinedAt(when);members.save(m);
    }
    private PrivateGroup authorized(String actor,Long id){
        membership(actor,id);
        return groups.findById(id).orElseThrow(()->new AccessDeniedException("No accessible group"));
    }
    private PrivateGroupMember membership(String actor,Long id){
        if(id==null||id<=0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        var self=actor(actor);
        return members.findByGroupIdAndUsernameIgnoreCase(id,self.getUsername())
                .orElseThrow(()->new AccessDeniedException("No accessible group"));
    }
    private Profile actor(String username){
        return profiles.findUserByUsernameIgnoreCase(username)
             .filter(p->Boolean.TRUE.equals(p.getEnabled()) && p.getModerationSuspendedAt()==null
                 && !Boolean.FALSE.equals(p.getNotLocked()))
             .orElseThrow(()->new AccessDeniedException("Not an active user"));
    }
    private String validTitle(String name){
        if(name==null||name.trim().length()<2||name.trim().length()>90)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Group name must have 2–90 characters");
        return name.trim();
    }
    private List<Profile> invitees(Profile actor,List<String> targets){
        if(targets==null||targets.isEmpty()||targets.size()>=MAX_MEMBERS)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Choose 1–29 members");
        Set<String> names=new LinkedHashSet<>();
        List<Profile> result=new ArrayList<>();
        for(String item:targets){
            if(item==null||item.isBlank()||item.length()>128)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid participant");
            Profile peer=profiles.findUserByUsernameIgnoreCase(item.trim())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unknown participant"));
            if(!names.add(peer.getUsername().toLowerCase(Locale.ROOT))
                    ||peer.getUsername().equalsIgnoreCase(actor.getUsername()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Duplicate participant");
            // Reuse direct-chat friendship + block policy for every invitee.
            chatService.getChatInfo(actor.getUsername(),peer.getUsername());
            result.add(peer);
        }
        return result;
    }
}
