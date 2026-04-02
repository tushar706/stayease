package com.stayease.paymentservice.kafka;



import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent {
    private Long bookingId;
    private Long userId;
    private Long hotelId;
    private Long roomId;
    private BigDecimal totalPrice;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;
}


