package com.VuVietAnh.stadium_booking_api.service;

import java.util.Map;

public interface FcmService {
    void sendPushNotification(String fcmToken, String title, String body, Map<String, String> data);
}
