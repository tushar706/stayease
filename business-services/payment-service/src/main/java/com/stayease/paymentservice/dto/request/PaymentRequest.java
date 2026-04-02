package com.stayease.paymentservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotNull(message = "Booking id is required")
    private Long bookingId;

    @NotNull(message = "User id is required")
    private Long userId;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;
}
