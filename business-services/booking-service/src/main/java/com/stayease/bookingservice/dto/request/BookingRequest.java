package com.stayease.bookingservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequest {

    @NotNull(message = "User id is required")
    private Long userId;

    @NotNull(message = "Hotel id is required")
    private Long hotelId;

    @NotNull(message = "Room id is required")
    private Long roomId;

    @NotNull(message = "Check in date is required")
    @FutureOrPresent(message = "Check in date cannot be in past")
    private LocalDate checkInDate;

    @NotNull(message = "Check out date is required")
    @Future(message = "Check out date must be in future")
    private LocalDate checkOutDate;
}