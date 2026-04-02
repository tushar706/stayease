package com.stayease.paymentservice.controller;

import com.stayease.paymentservice.dto.request.PaymentRequest;
import com.stayease.paymentservice.dto.response.PaymentResponse;
import com.stayease.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest request) {
        log.info("Manual payment request for bookingId: {}",
                request.getBookingId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.createPayment(request));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse> getPaymentByBookingId(
            @PathVariable Long bookingId) {
        log.info("Get payment request for bookingId: {}", bookingId);
        return ResponseEntity.ok(
                paymentService.getPaymentByBookingId(bookingId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByUser(
            @PathVariable Long userId) {
        log.info("Get payments request for userId: {}", userId);
        return ResponseEntity.ok(
                paymentService.getPaymentsByUser(userId));
    }
}
