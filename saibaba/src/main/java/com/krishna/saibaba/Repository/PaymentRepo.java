package com.krishna.saibaba.Repository;

import com.krishna.saibaba.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, Integer> {
    List<Payment> findByCustomerId(Integer customerId);
    List<Payment> findByBookingId(Integer bookingId);
    Optional<Payment> findByTxnId(String txnId);
    List<Payment> findAll();
}