package com.stayease.paymentservice.repository;

import com.stayease.paymentservice.entity.Payment;
import com.stayease.paymentservice.entity.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Idempotency check — booking ka payment already hua?
    Optional<Payment> findByBookingId(Long bookingId);

    // User ke saare payments
    List<Payment> findByUserId(Long userId);

    // Status se payments
    List<Payment> findByStatus(PaymentStatus status);

    // Booking ka payment exist karta hai?
    boolean existsByBookingId(Long bookingId);
}
