package com.stayease.paymentservice.kafka;

import com.stayease.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final PaymentService paymentService;

    @KafkaListener(
            topics = "booking-created",
            groupId = "payment-group"
    )
    public void consumeBookingCreatedEvent(BookingEvent event) {
        log.info("Received booking-created event for bookingId: {}",
                event.getBookingId());

        try {
            paymentService.processPayment(event);
            log.info("Payment processed successfully for bookingId: {}",
                    event.getBookingId());
        } catch (Exception e) {
            log.error("Payment processing failed for bookingId: {} — {}",
                    event.getBookingId(), e.getMessage());
        }
    }

    @KafkaListener(
            topics = "booking-cancelled",
            groupId = "payment-group"
    )
    public void consumeBookingCancelledEvent(BookingEvent event) {
        log.info("Received booking-cancelled event for bookingId: {}",
                event.getBookingId());

        try {
            paymentService.refundPayment(event.getBookingId());
            log.info("Refund processed successfully for bookingId: {}",
                    event.getBookingId());
        } catch (Exception e) {
            log.error("Refund processing failed for bookingId: {} — {}",
                    event.getBookingId(), e.getMessage());
        }
    }
}
