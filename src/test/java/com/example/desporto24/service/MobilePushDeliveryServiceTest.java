package com.example.desporto24.service;

import com.example.desporto24.model.dto.NotificationPreferencesRequest;
import com.example.desporto24.repository.MobilePushDeviceRepository;
import com.example.desporto24.repository.MobilePushReceiptRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Set;
import static org.mockito.Mockito.*;

class MobilePushDeliveryServiceTest {
    private final MobilePushDeviceRepository devices=mock(MobilePushDeviceRepository.class);
    private final MobilePushReceiptRepository receipts=mock(MobilePushReceiptRepository.class);
    private final NotificationPreferencesService preferences=mock(NotificationPreferencesService.class);
    private final ConversationNotificationMuteService mutes=mock(ConversationNotificationMuteService.class);
    private final MobilePushDeliveryService sender=
            new MobilePushDeliveryService(devices,receipts,preferences,mutes,new ObjectMapper());

    @Test
    void disabledByDefaultNeverLooksUpDeviceTokens() {
        ReflectionTestUtils.setField(sender,"enabled",false);
        ReflectionTestUtils.setField(sender,"accessToken","test-only");
        sender.afterCommit(new MobilePushDeliveryService.StoredNotification("player",42L,"CHAT",null,null,null,true));
        verifyNoInteractions(devices,preferences);
    }

    @Test
    void missingExpoCredentialFailsClosedEvenIfFlagEnabled() {
        ReflectionTestUtils.setField(sender,"enabled",true);
        ReflectionTestUtils.setField(sender,"accessToken","");
        sender.afterCommit(new MobilePushDeliveryService.StoredNotification("player",42L,"CHAT",null,null,null,true));
        verifyNoInteractions(devices,preferences);
    }

    @Test
    void accountPreferencesCanSuppressPushWithoutQueryingDevices() {
        ReflectionTestUtils.setField(sender,"enabled",true);
        ReflectionTestUtils.setField(sender,"accessToken","test-only");
        when(preferences.get("player")).thenReturn(
                new NotificationPreferencesRequest(true,true,Set.of("CHAT"),Set.of()));
        sender.afterCommit(new MobilePushDeliveryService.StoredNotification("PLAYER",42L,"CHAT",null,null,null,true));
        verifyNoInteractions(devices);
    }

    @Test
    void mutedChatBlocksLookupAndDelivery() {
        ReflectionTestUtils.setField(sender,"enabled",true);
        ReflectionTestUtils.setField(sender,"accessToken","test-only");
        when(preferences.get("player")).thenReturn(
                new NotificationPreferencesRequest(true,true,Set.of(),Set.of()));
        when(mutes.muted("player","community","12")).thenReturn(true);
        sender.afterCommit(new MobilePushDeliveryService.StoredNotification(
                "player",42L,"CHAT_MESSAGE",null,12L,null,true));
        verifyNoInteractions(devices);
    }

    @Test
    void mutedPrivateGroupBlocksLookupAndDelivery() {
        ReflectionTestUtils.setField(sender,"enabled",true);
        ReflectionTestUtils.setField(sender,"accessToken","test-only");
        when(preferences.get("player")).thenReturn(
                new NotificationPreferencesRequest(true,true,Set.of(),Set.of()));
        when(mutes.muted("player","private","5")).thenReturn(true);
        sender.afterCommit(new MobilePushDeliveryService.StoredNotification(
                "player",42L,"CHAT_MESSAGE",null,null,"someone",true,5L));
        verify(mutes).muted("player","private","5");
        verifyNoInteractions(devices);
    }

    @Test
    void unscopedChatCannotSendPush() {
        ReflectionTestUtils.setField(sender,"enabled",true);
        ReflectionTestUtils.setField(sender,"accessToken","test-only");
        when(preferences.get("player")).thenReturn(
                new NotificationPreferencesRequest(true,true,Set.of(),Set.of()));
        sender.afterCommit(new MobilePushDeliveryService.StoredNotification(
                "player",42L,"CHAT_MESSAGE",null,null,null,true));
        verifyNoInteractions(devices,mutes);
    }

    @Test
    void sessionCancellationRespectsNotificationCategoryOptOut() {
        ReflectionTestUtils.setField(sender,"enabled",true);
        ReflectionTestUtils.setField(sender,"accessToken","test-only");
        when(preferences.get("player")).thenReturn(
                new NotificationPreferencesRequest(true,true,Set.of("SESSION_CANCELLED"),Set.of()));
        sender.afterCommit(new MobilePushDeliveryService.StoredNotification(
                "player",42L,"SESSION_CANCELLED",14L,null,null,true));
        verifyNoInteractions(devices,mutes);
    }

    @Test
    void cancellationPushIsNotSentIfStoredAlertWasDisallowed() {
        ReflectionTestUtils.setField(sender,"enabled",true);
        ReflectionTestUtils.setField(sender,"accessToken","test-only");
        sender.afterCommit(new MobilePushDeliveryService.StoredNotification(
                "player",42L,"SESSION_CANCELLED",14L,null,null,false));
        verifyNoInteractions(devices,preferences,mutes);
    }

    @Test
    void unaddressableNotificationsNeverAttemptPushDelivery() {
        ReflectionTestUtils.setField(sender,"enabled",true);
        ReflectionTestUtils.setField(sender,"accessToken","test-only");
        sender.afterCommit(new MobilePushDeliveryService.StoredNotification(
                "player",null,"SESSION_CANCELLED",14L,null,null,true));
        sender.afterCommit(new MobilePushDeliveryService.StoredNotification(
                "player",0L,"SESSION_CANCELLED",14L,null,null,true));
        sender.afterCommit(new MobilePushDeliveryService.StoredNotification(
                "player",-1L,"SESSION_CANCELLED",14L,null,null,true));
        sender.afterCommit(new MobilePushDeliveryService.StoredNotification(
                "player",9_007_199_254_740_992L,"SESSION_CANCELLED",14L,null,null,true));
        verifyNoInteractions(devices,preferences,mutes,receipts);
    }

    @Test
    void globallyDisabledInAppNotificationsSuppressDelivery() {
        ReflectionTestUtils.setField(sender,"enabled",true);
        ReflectionTestUtils.setField(sender,"accessToken","test-only");
        when(preferences.get("player")).thenReturn(
                new NotificationPreferencesRequest(false,true,Set.of(),Set.of()));
        sender.afterCommit(new MobilePushDeliveryService.StoredNotification("player",42L,"OTHER",null,null,null,true));
        verifyNoInteractions(devices);
    }

    @Test
    void lockScreenTextIsSpecificButNeverPrivate() {
        org.junit.jupiter.api.Assertions.assertEquals("Lembrete de jogo", MobilePushDeliveryService.text("SESSION_REMINDER")[0]);
        org.junit.jupiter.api.Assertions.assertEquals("Nova mensagem", MobilePushDeliveryService.text("CHAT_MESSAGE")[0]);
        org.junit.jupiter.api.Assertions.assertEquals("Jynlo", MobilePushDeliveryService.text(null)[0]);
        for (var type : com.example.desporto24.enumeration.NotificationType.values()) {
            String[] text = MobilePushDeliveryService.text(type.name());
            org.junit.jupiter.api.Assertions.assertEquals(2, text.length);
            org.junit.jupiter.api.Assertions.assertFalse(text[0].isBlank() || text[1].isBlank());
        }
    }
}
