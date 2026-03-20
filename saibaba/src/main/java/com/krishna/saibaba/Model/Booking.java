package com.krishna.saibaba.Model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer bookingId;

    Integer customerId;
    Integer providerId;
    Integer serviceId;
    LocalDate bookingDate;
    String address;
    String status;
    String otp;
    LocalDateTime otpExpiry;
    Boolean isVerified;
    private Integer rating;
    private String review;

    // ✅ New fields for payment tracking
    private Double amount;
    private String paymentStatus; // SUCCESS, COD, PENDING, null

    public Booking() {}

    @PrePersist
    public void setDefaultValues() {
        this.bookingDate = LocalDate.now();
        this.isVerified = false;
        // amount will be set from service price in BookingService.CreateBooking()
    }

    // ── Getters & Setters ──

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public LocalDateTime getOtpExpiry() { return otpExpiry; }
    public void setOtpExpiry(LocalDateTime otpExpiry) { this.otpExpiry = otpExpiry; }

    public Boolean isVerified() { return isVerified; }
    public void setVerified(Boolean verified) { isVerified = verified; }
    public Boolean getVerified() { return isVerified; }

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public Integer getProviderId() { return providerId; }
    public void setProviderId(Integer providerId) { this.providerId = providerId; }

    public Integer getServiceId() { return serviceId; }
    public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
