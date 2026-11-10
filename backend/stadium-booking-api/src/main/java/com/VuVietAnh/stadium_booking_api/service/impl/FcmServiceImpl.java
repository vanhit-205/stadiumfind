package com.VuVietAnh.stadium_booking_api.service.impl;

import com.VuVietAnh.stadium_booking_api.service.FcmService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class FcmServiceImpl implements FcmService {

    private final FirebaseMessaging firebaseMessaging;

    public FcmServiceImpl(@Autowired(required = false) FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    @Override
    public void sendPushNotification(String fcmToken, String title, String body, Map<String, String> data) {
        if (fcmToken == null || fcmToken.isBlank()) {
            log.warn("Cannot send FCM notification: target user has no FCM token registered.");
            return;
        }

        if (firebaseMessaging == null) {
            log.warn("FirebaseMessaging is not initialized. Skipping push notification to token: {}", fcmToken);
            return;
        }

        try {
            Message.Builder messageBuilder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());

            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            Message message = messageBuilder.build();
            String response = firebaseMessaging.send(message);
            log.info("Successfully sent FCM push notification. Message ID: {}", response);
        } catch (Exception e) {
            log.error("Error sending FCM push notification to token {}: {}", fcmToken, e.getMessage());
        }
    }
}
