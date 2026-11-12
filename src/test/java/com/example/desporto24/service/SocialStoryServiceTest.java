package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.enumeration.CommunityVisibility;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityMember;
import com.example.desporto24.model.dto.social.StoryResponse;
import com.example.desporto24.model.social.SocialStory;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.community.CommunityRepository;
import com.example.desporto24.repository.social.ProfileFollowRepository;
import com.example.desporto24.repository.social.SocialStoryRepository;
import com.example.desporto24.repository.social.SocialStoryLikeRepository;
import com.example.desporto24.repository.social.SocialStoryReplyRepository;
import org.springframework.context.ApplicationEventPublisher;
import com.example.desporto24.storage.MediaStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocialStoryServiceTest {
    @Mock private SocialStoryRepository stories;
    @Mock private SocialStoryLikeRepository likes;
    @Mock private SocialStoryReplyRepository replies;
    @Mock private ApplicationEventPublisher events;
    @Mock private UserBlockService userBlockService;
    @Mock private ProfileRepository profiles;
    @Mock private CommunityRepository communities;
    @Mock private CommunityMemberRepository memberships;
    @Mock private ProfileFollowRepository follows;
    @Mock private MediaStorage storage;
    @InjectMocks private SocialStoryService service;

    private Profile actor;

    @BeforeEach
    void init() {
        actor = new Profile();
        actor.setId(19L);
        actor.setUsername("joao");
        when(profiles.findUserByUsernameIgnoreCase("joao")).thenReturn(Optional.of(actor));
    }

    @Test
    void acceptsOnePictureAndExpiresExactly24HoursAfterCreation() {
        MockMultipartFile image = new MockMultipartFile("image", "photo.jpg", "image/jpeg", new byte[] {1,2,3});
        when(storage.store(startsWith("stories/19/"), same(image)))
                .thenReturn(URI.create("/media/stories/19/photo.jpg"));
        when(stories.save(any(SocialStory.class))).thenAnswer(call -> {
            SocialStory story = call.getArgument(0);
            story.setId(7L);
            return story;
        });

        StoryResponse response = service.create("joao", null, "Primeiro jogo!", image, null);

        assertEquals(7L, response.id());
        assertEquals("Primeiro jogo!", response.caption());
        assertNull(response.communityId());
        assertTrue(response.canDelete());
        assertFalse(response.video());
        assertEquals(24, ChronoUnit.HOURS.between(response.createdAt(), response.expiresAt()));
        assertEquals(0, ChronoUnit.MINUTES.between(response.createdAt(), response.expiresAt()) % 60);
    }

    @Test
    void rejectsMissingAndConflictingMediaBeforeSaving() {
        MockMultipartFile image = new MockMultipartFile("image", "a.jpg", "image/jpeg", new byte[]{1});
        MockMultipartFile video = new MockMultipartFile("video", "a.mp4", "video/mp4", new byte[]{1});
        assertThrows(IllegalArgumentException.class, () -> service.create("joao", null, "", null, null));
        assertThrows(IllegalArgumentException.class, () -> service.create("joao", null, "", image, video));
        verifyNoInteractions(storage, stories);
    }

    @Test
    void rejectsRegularMemberPublishingOnBehalfOfCommunity() {
        Community community = publicCommunity();
        CommunityMember member = new CommunityMember();
        member.setRole(CommunityRole.MEMBER);
        when(communities.findById(5L)).thenReturn(Optional.of(community));
        when(memberships.findByCommunityIdAndProfileId(5L, 19L)).thenReturn(Optional.of(member));
        MockMultipartFile image = new MockMultipartFile("image", "a.jpg", "image/jpeg", new byte[]{1});

        assertThrows(AccessDeniedException.class, () -> service.create("joao", 5L, "Olá", image, null));
        verifyNoInteractions(storage, stories);
    }

    @Test
    void neverUploadsPrivateCommunityMediaToPublicStorage() {
        Community community = publicCommunity();
        community.setVisibility(CommunityVisibility.PRIVATE);
        community.setOwner(actor);
        when(communities.findById(5L)).thenReturn(Optional.of(community));
        MockMultipartFile image = new MockMultipartFile("image", "a.jpg", "image/jpeg", new byte[]{1});

        assertThrows(AccessDeniedException.class, () -> service.create("joao", 5L, "Olá", image, null));
        verifyNoInteractions(storage, stories);
    }

    @Test
    void storyFeedExcludesBlockedAuthorsEvenWhenCommunityIsFollowed() {
        when(userBlockService.blockedProfileIds(actor)).thenReturn(java.util.Set.of(33L, 44L));
        when(stories.findActiveFeed(any(Instant.class), anyCollection(), anyCollection(),
                anyCollection(), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(java.util.List.of());
        assertTrue(service.feed("joao").isEmpty());
        verify(stories).findActiveFeed(any(Instant.class),
                argThat(ids -> ids.contains(19L) && !ids.contains(33L)),
                anyCollection(),
                argThat(ids -> ids.contains(33L) && ids.contains(44L)),
                any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void storyFeedUsesSafePlaceholderWhenNoProfilesAreBlocked() {
        when(userBlockService.blockedProfileIds(actor)).thenReturn(java.util.Set.of());
        when(stories.findActiveFeed(any(Instant.class), anyCollection(), anyCollection(),
                anyCollection(), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(java.util.List.of());
        assertTrue(service.feed("joao").isEmpty());
        verify(stories).findActiveFeed(any(Instant.class), anyCollection(),
                anyCollection(), argThat(ids -> ids.size()==1 && ids.contains(-1L)),
                any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void myStoriesQueryIsScopedToAuthenticatedAuthorAndActiveWindow() {
        when(stories.findTop20ByAuthorIdAndExpiresAtAfterOrderByCreatedAtDesc(eq(19L), any(Instant.class)))
                .thenReturn(java.util.List.of());
        assertTrue(service.mine("joao").isEmpty());
        verify(stories).findTop20ByAuthorIdAndExpiresAtAfterOrderByCreatedAtDesc(eq(19L), any(Instant.class));
    }

    @Test
    void refusesAccessToExpiredStory() {
        SocialStory story = SocialStory.builder().id(4L).author(actor)
                .createdAt(Instant.now().minus(25, ChronoUnit.HOURS))
                .expiresAt(Instant.now().minus(1, ChronoUnit.HOURS))
                .mediaUrl("/media/stories/a.jpg").mediaStorageKey("stories/a.jpg")
                .build();
        when(stories.findById(4L)).thenReturn(Optional.of(story));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.view(4L, "joao"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void storyReplyIsPrivateNotificationToAuthorNotPublicComment() {
        Profile owner = new Profile();
        owner.setId(20L);
        owner.setUsername("maria");
        SocialStory story = SocialStory.builder().id(5L).author(owner)
                .createdAt(Instant.now()).expiresAt(Instant.now().plusSeconds(3600))
                .build();
        when(stories.findById(5L)).thenReturn(Optional.of(story));
        service.reply(5L, "joao", "Grande jogada!");
        verify(replies).save(argThat(reply -> reply.getStoryId().equals(5L)
                && reply.getRecipient().getUsername().equals("maria")
                && reply.getSender().getUsername().equals("joao")
                && reply.getContent().equals("Grande jogada!")));
        verify(events).publishEvent((Object) argThat((SocialStoryNotificationDispatcher.StoryNotice n) ->
                n.authorUsername().equals("maria") && n.senderUsername().equals("joao")
                && n.message().contains("Grande jogada!")));
    }

    @Test
    void storyReplyRejectsSelfAndExpiredStory() {
        SocialStory story = SocialStory.builder().id(6L).author(actor)
                .createdAt(Instant.now()).expiresAt(Instant.now().plusSeconds(3600)).build();
        when(stories.findById(6L)).thenReturn(Optional.of(story));
        assertThrows(IllegalArgumentException.class, () -> service.reply(6L, "joao", "Olá"));
        verifyNoInteractions(events);
    }

    @Test
    void storyLikeCanBeToggledAndNotifiesOnlyOnFirstLike() {
        Profile owner = new Profile();
        owner.setId(20L);
        owner.setUsername("maria");
        SocialStory story = SocialStory.builder().id(7L).author(owner)
                .createdAt(Instant.now()).expiresAt(Instant.now().plusSeconds(3600)).build();
        when(stories.findByIdForInteraction(7L)).thenReturn(Optional.of(story));
        when(likes.existsByStoryIdAndProfileId(7L, 19L)).thenReturn(false, true, true);
        when(likes.countByStoryId(7L)).thenReturn(1L);
        StoryResponse first = service.like(7L, "joao");
        assertTrue(first.likedByMe());
        assertEquals(1L, first.likeCount());
        service.like(7L, "joao");
        verify(likes, times(1)).save(any());
        verify(events, times(1)).publishEvent((Object) argThat((SocialStoryNotificationDispatcher.StoryNotice n) ->
                n.storyId().equals(7L)
                && n.type() == com.example.desporto24.enumeration.NotificationType.SOCIAL_STORY_LIKED));
        service.unlike(7L, "joao");
        verify(likes).deleteByStoryIdAndProfileId(7L, 19L);
        verify(stories, times(3)).findByIdForInteraction(7L);
    }

    @Test
    void interactionLockRechecksStoryExpiryBeforeCreatingLike() {
        Profile owner = new Profile();
        owner.setId(20L);
        owner.setUsername("maria");
        SocialStory expired = SocialStory.builder().id(77L).author(owner)
                .createdAt(Instant.now().minus(25, ChronoUnit.HOURS))
                .expiresAt(Instant.now().minusSeconds(1)).build();
        when(stories.findByIdForInteraction(77L)).thenReturn(Optional.of(expired));
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.like(77L, "joao"));
        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verifyNoInteractions(likes, events);
    }

    @Test
    void blockedStoryInteractionDoesNotSaveLikeOrSendNotification() {
        Profile owner = new Profile();
        owner.setId(30L);
        owner.setUsername("ines");
        SocialStory story = SocialStory.builder().id(88L).author(owner)
                .createdAt(Instant.now()).expiresAt(Instant.now().plusSeconds(3600)).build();
        when(stories.findByIdForInteraction(88L)).thenReturn(Optional.of(story));
        when(userBlockService.isBlockedBetween(actor, owner)).thenReturn(true);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.like(88L, "joao"));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verifyNoInteractions(likes, events);
    }

    @Test
    void storyRepliesRejectEmptyAndExcessivelyLongText() {
        Profile owner = new Profile();
        owner.setId(20L);
        owner.setUsername("maria");
        SocialStory story = SocialStory.builder().id(8L).author(owner)
                .createdAt(Instant.now()).expiresAt(Instant.now().plusSeconds(3600)).build();
        when(stories.findById(8L)).thenReturn(Optional.of(story));
        assertThrows(IllegalArgumentException.class, () -> service.reply(8L, "joao", "  "));
        assertThrows(IllegalArgumentException.class,
                () -> service.reply(8L, "joao", "x".repeat(501)));
        verifyNoInteractions(events);
    }

    @Test
    void privateStoryReplyPersistsIndependentlyOfOptionalNotificationProcessing() {
        Profile owner = new Profile();
        owner.setId(20L);
        owner.setUsername("maria");
        SocialStory story = SocialStory.builder().id(9L).author(owner)
                .createdAt(Instant.now()).expiresAt(Instant.now().plusSeconds(3600)).build();
        when(stories.findById(9L)).thenReturn(Optional.of(story));
        service.reply(9L, "joao", "Boa defesa!");
        verify(replies).save(argThat(reply -> reply.getRecipient().getId().equals(20L)
                && reply.getContent().equals("Boa defesa!")));
        verify(events).publishEvent((Object) argThat((SocialStoryNotificationDispatcher.StoryNotice n) ->
                n.storyId().equals(9L) && n.senderUsername().equals("joao")));
    }

    @Test
    void privateStoryReplyHistoryIsScopedToAuthenticatedRecipient() {
        com.example.desporto24.model.social.SocialStoryReply item =
                com.example.desporto24.model.social.SocialStoryReply.builder()
                .id(13L).storyId(9L).sender(actor).recipient(actor)
                .content("Olá").createdAt(Instant.now()).build();
        when(replies.findVisibleReplies(eq(19L), anyCollection(),
                any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(java.util.List.of(item));
        var result = service.myPrivateReplies("joao", 1);
        assertEquals(1, result.size());
        assertEquals("joao", result.get(0).senderUsername());
        verify(replies).findVisibleReplies(eq(19L),
                argThat(ids -> ids.size()==1 && ids.contains(-1L)),
                argThat(page -> page.getPageNumber()==1 && page.getPageSize()==20));
    }

    @Test
    void blockedPrivateStorySendersAreFilteredBeforePagination() {
        when(userBlockService.blockedProfileIds(actor)).thenReturn(java.util.Set.of(44L, 55L));
        when(replies.findVisibleReplies(eq(19L), anyCollection(),
                any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(java.util.List.of());

        assertTrue(service.myPrivateReplies("joao", 2).isEmpty());
        verify(replies).findVisibleReplies(eq(19L),
                argThat(ids -> ids.contains(44L) && ids.contains(55L)),
                argThat(page -> page.getPageNumber()==2 && page.getPageSize()==20));
    }

    @Test
    void storyDeepLinkHidesContentWhenAuthorIsBlocked() {
        Profile owner = new Profile();
        owner.setId(22L);
        owner.setUsername("rui");
        SocialStory story = SocialStory.builder().id(31L).author(owner)
                .createdAt(Instant.now()).expiresAt(Instant.now().plusSeconds(3600)).build();
        when(stories.findById(31L)).thenReturn(Optional.of(story));
        when(userBlockService.isBlockedBetween(actor, owner)).thenReturn(true);
        ResponseStatusException hidden = assertThrows(ResponseStatusException.class,
                () -> service.view(31L, "joao"));
        assertEquals(HttpStatus.NOT_FOUND, hidden.getStatusCode());
        verifyNoInteractions(likes);
    }

    private Community publicCommunity() {
        Profile owner = new Profile();
        owner.setId(200L);
        Community community = new Community();
        community.setId(5L);
        community.setName("CNG");
        community.setOwner(owner);
        community.setVisibility(CommunityVisibility.PUBLIC);
        return community;
    }
}
