package com.stayease.bookingservice.service;

import com.stayease.bookingservice.dto.request.BookingRequest;
import com.stayease.bookingservice.dto.response.BookingResponse;
import com.stayease.bookingservice.entity.Booking;
import com.stayease.bookingservice.entity.Booking.BookingStatus;
import com.stayease.bookingservice.exception.BookingNotFoundException;
import com.stayease.bookingservice.exception.RoomNotAvailableException;
import com.stayease.bookingservice.kafka.BookingEvent;
import com.stayease.bookingservice.kafka.BookingEventPublisher;
import com.stayease.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingEventPublisher eventPublisher;
    private final RedissonClient redissonClient;

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        String lockKey = "room:" + request.getRoomId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // Step 1 — Redis Lock lagao
            boolean isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);

            if (!isLocked) {
                throw new RoomNotAvailableException(
                        "Room is currently being booked. Please try again.");
            }

            log.info("Redis lock acquired for room: {}", request.getRoomId());

            // Step 2 — Overlap check karo
            List<Booking> overlapping = bookingRepository
                    .findOverlappingBookings(
                            request.getRoomId(),
                            request.getCheckInDate(),
                            request.getCheckOutDate()
                    );

            if (!overlapping.isEmpty()) {
                throw new RoomNotAvailableException(
                        "Room is not available for selected dates.");
            }

            // Step 3 — Total price calculate karo
            long nights = ChronoUnit.DAYS.between(
                    request.getCheckInDate(),
                    request.getCheckOutDate()
            );

            // Hardcode price for now
            // Production mein Hotel Service se fetch karenge
            BigDecimal pricePerNight = new BigDecimal("2000.00");
            BigDecimal totalPrice = pricePerNight.multiply(
                    BigDecimal.valueOf(nights));

            // Step 4 — Booking save karo
            Booking booking = Booking.builder()
                    .userId(request.getUserId())
                    .hotelId(request.getHotelId())
                    .roomId(request.getRoomId())
                    .checkInDate(request.getCheckInDate())
                    .checkOutDate(request.getCheckOutDate())
                    .totalPrice(totalPrice)
                    .status(BookingStatus.PENDING)
                    .build();

            Booking savedBooking = bookingRepository.save(booking);
            log.info("Booking saved with id: {}", savedBooking.getId());

            // Step 5 — Kafka event publish karo
            BookingEvent event = BookingEvent.builder()
                    .bookingId(savedBooking.getId())
                    .userId(savedBooking.getUserId())
                    .hotelId(savedBooking.getHotelId())
                    .roomId(savedBooking.getRoomId())
                    .totalPrice(savedBooking.getTotalPrice())
                    .checkInDate(savedBooking.getCheckInDate())
                    .checkOutDate(savedBooking.getCheckOutDate())
                    .status(savedBooking.getStatus().name())
                    .build();

            eventPublisher.publishBookingCreated(event);

            return mapToResponse(savedBooking);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RoomNotAvailableException("Booking interrupted. Try again.");
        } finally {
            // Step 6 — Lock release karo — hamesha!
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("Redis lock released for room: {}", request.getRoomId());
            }
        }
    }

    public BookingResponse getBookingById(Long id) {
        log.info("Fetching booking with id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking not found with id: " + id));
        return mapToResponse(booking);
    }

    public List<BookingResponse> getBookingsByUser(Long userId) {
        log.info("Fetching bookings for user: {}", userId);
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse cancelBooking(Long id) {
        log.info("Cancelling booking with id: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking not found with id: " + id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking savedBooking = bookingRepository.save(booking);

        // Kafka pe cancellation event publish karo
        BookingEvent event = BookingEvent.builder()
                .bookingId(savedBooking.getId())
                .userId(savedBooking.getUserId())
                .hotelId(savedBooking.getHotelId())
                .roomId(savedBooking.getRoomId())
                .totalPrice(savedBooking.getTotalPrice())
                .status(BookingStatus.CANCELLED.name())
                .build();

        eventPublisher.publishBookingCancelled(event);
        log.info("Booking cancelled successfully: {}", id);

        return mapToResponse(savedBooking);
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .hotelId(booking.getHotelId())
                .roomId(booking.getRoomId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}