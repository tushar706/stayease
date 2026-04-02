package com.stayease.bookingservice.controller;

import com.stayease.bookingservice.dto.request.BookingRequest;
import com.stayease.bookingservice.dto.response.BookingResponse;
import com.stayease.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request) {
        log.info("Create booking request received for room: {}",
                request.getRoomId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingService.createBooking(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long id) {
        log.info("Get booking request received for id: {}", id);
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByUser(
            @PathVariable Long userId) {
        log.info("Get bookings request received for user: {}", userId);
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id) {
        log.info("Cancel booking request received for id: {}", id);
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }
}
