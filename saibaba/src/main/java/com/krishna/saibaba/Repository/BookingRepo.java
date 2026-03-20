package com.krishna.saibaba.Repository;

import com.krishna.saibaba.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepo extends JpaRepository<Booking,Integer> {
    Object findByBookingId(Integer bookingId);

    List<Booking> findByCustomerId(Integer customerId);


    List<Booking> findByProviderId(Integer providerId);

    List<Booking> findByStatusAndOtpExpiryBefore(String waitingForOtp, LocalDateTime now);
}
