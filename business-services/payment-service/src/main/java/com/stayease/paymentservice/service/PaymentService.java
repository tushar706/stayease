package com.stayease.paymentservice.service;

import com.stayease.paymentservice.dto.request.PaymentRequest;
import com.stayease.paymentservice.dto.response.PaymentResponse;
import com.stayease.paymentservice.entity.Payment;
import com.stayease.paymentservice.entity.Payment.PaymentStatus;
import com.stayease.paymentservice.exception.PaymentAlreadyProcessedException;
import com.stayease.paymentservice.exception.PaymentNotFoundException;
import com.stayease.paymentservice.kafka.BookingEvent;
import com.stayease.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    // Kafka se automatically call hoga
    @Transactional
    public void processPayment(BookingEvent event) {
        log.info("Processing payment for bookingId: {}", event.getBookingId());

        // Idempotency check — duplicate event check
        if (paymentRepository.existsByBookingId(event.getBookingId())) {
            log.warn("Payment already processed for bookingId: {}",
                    event.getBookingId());
            return;  // Duplicate — skip karo
        }

        try {
            // Payment simulate karo
            // Production mein Razorpay/Stripe call hoga
            Payment payment = Payment.builder()
                    .bookingId(event.getBookingId())
                    .userId(event.getUserId())
                    .amount(event.getTotalPrice())
                    .status(PaymentStatus.SUCCESS)
                    .build();

            paymentRepository.save(payment);
            log.info("Payment saved successfully for bookingId: {}",
                    event.getBookingId());

        } catch (Exception e) {
            // Payment fail hua — save karo reason ke saath
            Payment failedPayment = Payment.builder()
                    .bookingId(event.getBookingId())
                    .userId(event.getUserId())
                    .amount(event.getTotalPrice())
                    .status(PaymentStatus.FAILED)
                    .failureReason(e.getMessage())
                    .build();

            paymentRepository.save(failedPayment);
            log.error("Payment failed for bookingId: {}", event.getBookingId());
        }
    }

    @Transactional
    public void refundPayment(Long bookingId) {
        log.info("Processing refund for bookingId: {}", bookingId);

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found for bookingId: " + bookingId));

        if (payment.getStatus() == PaymentStatus.FAILED) {
            log.warn("Cannot refund failed payment for bookingId: {}", bookingId);
            return;
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason("Booking cancelled — refund processed");
        paymentRepository.save(payment);

        log.info("Refund processed for bookingId: {}", bookingId);
    }

    // Manual payment — REST API se
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        log.info("Manual payment request for bookingId: {}",
                request.getBookingId());

        if (paymentRepository.existsByBookingId(request.getBookingId())) {
            throw new PaymentAlreadyProcessedException(
                    "Payment already processed for bookingId: "
                            + request.getBookingId());
        }

        Payment payment = Payment.builder()
                .bookingId(request.getBookingId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .status(PaymentStatus.SUCCESS)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return mapToResponse(savedPayment);
    }

    public PaymentResponse getPaymentByBookingId(Long bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found for bookingId: " + bookingId));
        return mapToResponse(payment);
    }

    public List<PaymentResponse> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
