package com.stayease.paymentservice.dto.response;

import com.stayease.paymentservice.entity.Payment.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long bookingId;
    private Long userId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String failureReason;
    private LocalDateTime createdAt;
}
