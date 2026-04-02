package com.stayease.bookingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class BookingServiceApplication {
    public static void main(String[] args) {
        // Ensure PostgreSQL startup packet uses a valid, deterministic timezone (Postgres may reject aliases like Asia/Calcutta).
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(BookingServiceApplication.class, args);
    }
}
