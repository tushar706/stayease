package com.stayease.bookingservice.repository;

import com.stayease.bookingservice.entity.Booking;
import com.stayease.bookingservice.entity.Booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // all booking from user
    List<Booking> findByUserId(Long userId);

    // User ki specific status ki bookings
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);


    @Query("SELECT b FROM Booking b " +
            "WHERE b.roomId = :roomId " +
            "AND b.status != 'CANCELLED' " +
            "AND (b.checkInDate < :checkOut " +
            "AND b.checkOutDate > :checkIn)")
    List<Booking> findOverlappingBookings(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);


    List<Booking> findByHotelId(Long hotelId);
}
