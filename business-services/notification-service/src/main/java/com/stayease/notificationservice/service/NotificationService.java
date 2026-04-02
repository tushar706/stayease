package com.stayease.notificationservice.service;

import com.stayease.notificationservice.kafka.BookingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void sendBookingConfirmation(BookingEvent event) {
        // Production mein Email/SMS service call hoga
        // Abhi simulate kar rahe hain
        log.info("════════════════════════════════════");
        log.info("📧 BOOKING CONFIRMATION EMAIL");
        log.info("To User ID  : {}", event.getUserId());
        log.info("Booking ID  : {}", event.getBookingId());
        log.info("Hotel ID    : {}", event.getHotelId());
        log.info("Check In    : {}", event.getCheckInDate());
        log.info("Check Out   : {}", event.getCheckOutDate());
        log.info("Total Price : ₹{}", event.getTotalPrice());
        log.info("Status      : {}", event.getStatus());
        log.info("════════════════════════════════════");
    }

    public void sendBookingCancellation(BookingEvent event) {
        log.info("════════════════════════════════════");
        log.info("📧 BOOKING CANCELLATION EMAIL");
        log.info("To User ID  : {}", event.getUserId());
        log.info("Booking ID  : {}", event.getBookingId());
        log.info("Refund Amt  : ₹{}", event.getTotalPrice());
        log.info("Status      : CANCELLED");
        log.info("════════════════════════════════════");
    }
}
