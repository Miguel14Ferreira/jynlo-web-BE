package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.chat.PrivateChatAttachment;
import com.example.desporto24.model.team.*;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.team.*;
import com.example.desporto24.storage.PrivateChatFileStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SportsTeamAttachmentServiceTest {
    private final SportsTeamRepository teams=mock(SportsTeamRepository.class);
    private final SportsTeamMemberRepository members=mock(SportsTeamMemberRepository.class);
    private final SportsTeamContentRepository messages=mock(SportsTeamContentRepository.class);
    private final ProfileRepository profiles=mock(ProfileRepository.class);
    private final PrivateChatFileStorage storage=mock(PrivateChatFileStorage.class);
    private final SportsTeamContentService service=
            new SportsTeamContentService(teams,members,messages,profiles);
    private Profile owner,player;
    private SportsTeam team;

    private Profile profile(long id,String username){
        var p=new Profile();p.setId(id);p.setUsername(username);return p;
    }
    @BeforeEach
    void setup(){
        ReflectionTestUtils.setField(service,"privateFiles",storage);
        owner=profile(1L,"owner");
        player=profile(2L,"player");
        team=SportsTeam.builder().id(42L).owner(owner)
                .chatEnabled(true).feedEnabled(true).build();
        when(teams.findById(42L)).thenReturn(Optional.of(team));
        when(profiles.findUserByUsername("owner")).thenReturn(Optional.of(owner));
        when(profiles.findUserByUsername("player")).thenReturn(Optional.of(player));
        when(storage.isConfigured()).thenReturn(true);
    }

    @Test
    void invitedPlayerCannotProbeUploadOrDownloadPrivateTeamFiles(){
        when(members.findByTeamIdAndProfileId(42L,2L)).thenReturn(Optional.of(
                SportsTeamMember.builder().team(team).profile(player)
                    .status(SportsTeamMember.Status.INVITED).build()));
        var file=new MockMultipartFile("files","test.pdf","application/pdf",
                "%PDF-1.7".getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        assertThrows(AccessDeniedException.class,()->
                service.attachmentsAvailable(42L,SportsTeamContent.Kind.CHAT,"player"));
        assertThrows(AccessDeniedException.class,()->
                service.upload(42L,SportsTeamContent.Kind.CHAT,List.of(file),
                        null,null,"player"));
        assertThrows(AccessDeniedException.class,()->
                service.download(42L,SportsTeamContent.Kind.CHAT,20L,0,"player"));
        verify(storage,never()).store(any());
        verify(storage,never()).read(anyString());
    }

    @Test
    void onePrivateFileHasMetadataButNeverLeaksStorageKey() {
        var file=new MockMultipartFile("files","jynlo.pdf","application/pdf",
                "%PDF-1.7".getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        when(storage.store(any())).thenReturn(
                new PrivateChatFileStorage.StoredFile("PRIVATE/KEY","jynlo.pdf","application/pdf",8));
        when(messages.saveAndFlush(any())).thenAnswer(invocation->{
            SportsTeamContent row=invocation.getArgument(0);row.setId(20L);return row;
        });
        var entry=service.upload(42L,SportsTeamContent.Kind.CHAT,
                List.of(file),"Documento",null,"owner");
        assertEquals(20L,entry.id());
        assertEquals(1,entry.attachments().size());
        assertEquals("jynlo.pdf",entry.attachments().get(0).filename());
        assertEquals("Documento",entry.body());
        assertFalse(entry.toString().contains("PRIVATE/KEY"));
        verify(storage).store(file);
    }

    @Test
    void privateDownloadRequiresMatchingTeamKindAndActiveMessage() {
        var row=SportsTeamContent.builder().id(20L).team(team).author(owner)
                .kind(SportsTeamContent.Kind.CHAT).body("Anexo")
                .attachmentKey("PRIVATE/KEY").attachmentName("jynlo.pdf")
                .attachmentMimeType("application/pdf").attachmentSize(8L)
                .createdAt(Instant.now()).build();
        when(messages.findById(20L)).thenReturn(Optional.of(row));
        when(storage.read("PRIVATE/KEY")).thenReturn(new byte[]{37,80,68,70});
        var file=service.download(42L,SportsTeamContent.Kind.CHAT,20L,0,"owner");
        assertEquals("jynlo.pdf",file.filename());
        assertEquals("application/pdf",file.mimeType());
        assertThrows(ResponseStatusException.class,()->
                service.download(42L,SportsTeamContent.Kind.FEED,20L,0,"owner"));
        row.setDeletedAt(Instant.now());
        assertThrows(ResponseStatusException.class,()->
                service.download(42L,SportsTeamContent.Kind.CHAT,20L,0,"owner"));
        verify(storage,times(1)).read("PRIVATE/KEY");
    }

    @Test
    void multiFileUploadChecksAllLimitsBeforeStoring(){
        var file=new MockMultipartFile("files","bad.pdf","application/pdf",new byte[1]);
        assertThrows(ResponseStatusException.class,()->
                service.upload(42L,SportsTeamContent.Kind.CHAT,
                        List.of(file,file,file,file,file),null,null,"owner"));
        verify(storage,never()).store(any());
    }

    @Test
    void failedSaveDeletesAllStoredPrivateObjects(){
        var file=new MockMultipartFile("files","jynlo.pdf","application/pdf",
                "%PDF-1.7".getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        when(storage.store(any())).thenReturn(new PrivateChatFileStorage.StoredFile(
                "private/a","jynlo.pdf","application/pdf",8));
        when(messages.saveAndFlush(any())).thenThrow(new IllegalStateException("db unavailable"));
        assertThrows(IllegalStateException.class,()->
                service.upload(42L,SportsTeamContent.Kind.CHAT,List.of(file),
                        null,null,"owner"));
        verify(storage).delete("private/a");
    }
}
