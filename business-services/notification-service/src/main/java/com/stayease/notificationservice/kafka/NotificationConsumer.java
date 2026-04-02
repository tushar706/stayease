package com.stayease.notificationservice.kafka;

import com.stayease.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "booking-created",
            groupId = "notification-group"
    )
    public void consumeBookingCreated(BookingEvent event) {
        log.info("Notification received for booking: {}",
                event.getBookingId());
        notificationService.sendBookingConfirmation(event);
    }

    @KafkaListener(
            topics = "booking-cancelled",
            groupId = "notification-group"
    )
    public void consumeBookingCancelled(BookingEvent event) {
        log.info("Cancellation notification for booking: {}",
                event.getBookingId());
        notificationService.sendBookingCancellation(event);
    }
}