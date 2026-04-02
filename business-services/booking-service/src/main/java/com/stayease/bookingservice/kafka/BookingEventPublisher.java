package com.stayease.bookingservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventPublisher {

    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    private static final String BOOKING_CREATED_TOPIC = "booking-created";
    private static final String BOOKING_CANCELLED_TOPIC = "booking-cancelled";

    public void publishBookingCreated(BookingEvent event) {
        log.info("Publishing booking created event: {}", event.getBookingId());
        kafkaTemplate.send(BOOKING_CREATED_TOPIC,
                String.valueOf(event.getBookingId()),
                event);
        log.info("Booking created event published successfully: {}",
                event.getBookingId());
    }

    public void publishBookingCancelled(BookingEvent event) {
        log.info("Publishing booking cancelled event: {}", event.getBookingId());
        kafkaTemplate.send(BOOKING_CANCELLED_TOPIC,
                String.valueOf(event.getBookingId()),
                event);
        log.info("Booking cancelled event published successfully: {}",
                event.getBookingId());
    }
}

