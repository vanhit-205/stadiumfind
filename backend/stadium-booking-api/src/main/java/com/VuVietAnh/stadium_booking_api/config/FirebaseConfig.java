package com.VuVietAnh.stadium_booking_api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.config.path:firebase/serviceAccountKey.json}")
    private String firebaseConfigPath;

    @Bean
    public FirebaseApp firebaseApp() {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        try {
            InputStream serviceAccount = null;
            Resource classPathResource = new ClassPathResource(firebaseConfigPath);
            if (classPathResource.exists()) {
                serviceAccount = classPathResource.getInputStream();
            } else {
                Resource fileResource = new FileSystemResource(firebaseConfigPath);
                if (fileResource.exists()) {
                    serviceAccount = fileResource.getInputStream();
                }
            }

            if (serviceAccount == null) {
                log.warn("Firebase credentials not found at: {}. Firebase push notifications will be disabled or mocked.", firebaseConfigPath);
                return null;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp app = FirebaseApp.initializeApp(options);
            log.info("FirebaseApp initialized successfully with configuration from: {}", firebaseConfigPath);
            return app;
        } catch (IOException e) {
            log.error("Failed to initialize FirebaseApp: {}", e.getMessage());
            return null;
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        if (firebaseApp == null) {
            log.warn("FirebaseApp is not initialized. FirebaseMessaging bean will be null.");
            return null;
        }
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
