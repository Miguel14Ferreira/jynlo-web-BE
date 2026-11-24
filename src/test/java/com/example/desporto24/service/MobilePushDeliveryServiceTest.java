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
        when(mutes.silenced("player","community","12")).thenReturn(true);
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
        when(mutes.silenced("player","private","5")).thenReturn(true);
        sender.afterCommit(new MobilePushDeliveryService.StoredNotification(
                "player",42L,"CHAT_MESSAGE",null,null,"someone",true,5L));
        verify(mutes).silenced("player","private","5");
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

    @Test
    void lockScreenTextFollowsTheDeviceLanguage() {
        org.junit.jupiter.api.Assertions.assertEquals("Game reminder", MobilePushDeliveryService.text("SESSION_REMINDER", "en")[0]);
        org.junit.jupiter.api.Assertions.assertEquals("Lembrete de jogo", MobilePushDeliveryService.text("SESSION_REMINDER", "pt")[0]);
        org.junit.jupiter.api.Assertions.assertEquals("Lembrete de jogo", MobilePushDeliveryService.text("SESSION_REMINDER", null)[0]);
        for (var type : com.example.desporto24.enumeration.NotificationType.values()) {
            String[] en = MobilePushDeliveryService.text(type.name(), "en");
            org.junit.jupiter.api.Assertions.assertFalse(en[0].isBlank() || en[1].isBlank());
            boolean ptGeneric = "Tens uma nova notificação.".equals(MobilePushDeliveryService.text(type.name())[1]);
            org.junit.jupiter.api.Assertions.assertEquals(ptGeneric, "You have a new notification.".equals(en[1]),
                    () -> "English and Portuguese push texts differ in coverage for " + type);
        }
        org.junit.jupiter.api.Assertions.assertEquals("en", MobilePushDeviceService.language("en-GB"));
        org.junit.jupiter.api.Assertions.assertEquals("pt", MobilePushDeviceService.language("fr"));
        org.junit.jupiter.api.Assertions.assertEquals("pt", MobilePushDeviceService.language(null));
    }

    @org.junit.jupiter.api.Test
    void chatPreviewShowsWhoWroteAndAShortText() {
        String[] direct = MobilePushDeliveryService.previewText("Nova mensagem de joao.fresco", "Forasteiros?");
        org.junit.jupiter.api.Assertions.assertEquals("joao.fresco", direct[0]);
        org.junit.jupiter.api.Assertions.assertEquals("Forasteiros?", direct[1]);
        String[] group = MobilePushDeliveryService.previewText("Novas mensagens em Padel Lisboa", "ana: vamos às 19h");
        org.junit.jupiter.api.Assertions.assertEquals("Padel Lisboa", group[0]);
        String[] longText = MobilePushDeliveryService.previewText("Nova mensagem em Equipa", "x".repeat(400));
        org.junit.jupiter.api.Assertions.assertTrue(longText[1].length() <= 178);
        org.junit.jupiter.api.Assertions.assertNull(MobilePushDeliveryService.previewText("Nova mensagem de a", " "));
    }
}
