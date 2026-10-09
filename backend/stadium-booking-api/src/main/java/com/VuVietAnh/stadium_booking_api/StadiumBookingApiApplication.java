package com.VuVietAnh.stadium_booking_api;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StadiumBookingApiApplication {

	public static void main(String[] args) {
		// Tự động nạp các biến cấu hình từ file .env vào System properties nếu có
		try {
			Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
			dotenv.entries().forEach(entry -> {
				if (System.getProperty(entry.getKey()) == null && System.getenv(entry.getKey()) == null) {
					System.setProperty(entry.getKey(), entry.getValue());
				}
			});
		} catch (Exception e) {
			System.err.println("Warning: Bỏ qua đọc .env: " + e.getMessage());
		}

		SpringApplication.run(StadiumBookingApiApplication.class, args);
	}

}
