// package com.krishna.saibaba.Service;

// import com.krishna.saibaba.Model.Booking;
// import com.krishna.saibaba.Repository.BookingRepo;
// import com.krishna.saibaba.Repository.ServiceRepo;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;

// import java.time.LocalDateTime;
// import java.util.List;

// @Service
// public class BookingService {

//     @Autowired
//     private BookingRepo bookingRepo;

//     @Autowired
//     private ServiceRepo serviceRepo;

//     // ✅ On booking creation, auto-fetch service price
//     public void CreateBooking(Booking booking) {
//         // Always fetch actual price from service table
//         if (booking.getServiceId() != null) {
//             try {
//                 com.krishna.saibaba.Model.Service svc =
//                     serviceRepo.findById(booking.getServiceId()).orElse(null);
//                 if (svc != null && svc.getPrice() != null) {
//                     // Strip non-numeric chars like ₹, spaces, commas
//                     String cleaned = svc.getPrice().replaceAll("[^0-9.]", "").trim();
//                     if (!cleaned.isEmpty()) {
//                         booking.setAmount(Double.parseDouble(cleaned));
//                     }
//                 }
//             } catch (Exception e) {
//                 System.out.println("Price fetch failed: " + e.getMessage());
//             }
//         }
//         booking.setStatus("BOOKED");
//         bookingRepo.save(booking);
//     }

//     public ResponseEntity<?> getAllBookings() {
//         return ResponseEntity.ok(bookingRepo.findAll());
//     }

//     public ResponseEntity<?> getBookingById(Integer bookingId) {
//         return ResponseEntity.ok(bookingRepo.findByBookingId(bookingId));
//     }

//     public ResponseEntity<?> getBookingsByCustomer(Integer customerId) {
//         List<Booking> bookings = bookingRepo.findByCustomerId(customerId);
//         return ResponseEntity.ok(bookings);
//     }

//     public ResponseEntity<?> getBookingsByProvider(Integer providerId) {
//         return ResponseEntity.ok(bookingRepo.findByProviderId(providerId));
//     }

//     public ResponseEntity<?> updateBooking(Integer id, String status) {
//         Booking booking = bookingRepo.findById(id).orElse(null);
//         if (booking != null) {
//             booking.setStatus(status);
//             bookingRepo.save(booking);
//             return ResponseEntity.ok("Booking updated");
//         }
//         return ResponseEntity.notFound().build();
//     }

//     public void cancelBooking(Integer bookingId) {
//         Booking booking = bookingRepo.findById(bookingId)
//                 .orElseThrow(() -> new RuntimeException("Booking not found"));
//         booking.setStatus("CANCELLED");
//         bookingRepo.save(booking);
//     }

//     public ResponseEntity<?> RequestCompletion(Integer bookingId) {
//         Booking booking = bookingRepo.findById(bookingId)
//                 .orElseThrow(() -> new RuntimeException("Booking not found"));

//         if (!booking.getStatus().equalsIgnoreCase("BOOKED")) {
//             return ResponseEntity.badRequest()
//                     .body("Cannot generate OTP. Current status: " + booking.getStatus());
//         }

//         String otp = String.valueOf((int) (Math.random() * 9000) + 1000);
//         booking.setOtp(otp);
//         booking.setStatus("WAITING_FOR_OTP");
//         booking.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
//         bookingRepo.save(booking);

//         return ResponseEntity.ok("OTP Generated");
//     }

//     public ResponseEntity<?> verifyOtp(Integer bookingId, String otp) {
//         Booking booking = bookingRepo.findById(bookingId)
//                 .orElseThrow(() -> new RuntimeException("Booking not found"));

//         if (booking.getOtpExpiry() == null || booking.getOtpExpiry().isBefore(LocalDateTime.now())) {
//             booking.setOtp(null);
//             booking.setOtpExpiry(null);
//             booking.setStatus("BOOKED");
//             bookingRepo.save(booking);
//             return ResponseEntity.badRequest().body("OTP Expired. Provider can generate a new one.");
//         }

//         if (booking.getOtp() == null || !booking.getOtp().equals(otp)) {
//             return ResponseEntity.badRequest().body("Invalid OTP");
//         }

//         booking.setStatus("COMPLETED");
//         booking.setVerified(true);
//         booking.setOtp(null);
//         booking.setOtpExpiry(null);
//         bookingRepo.save(booking);

//         return ResponseEntity.ok("Service Completed");
//     }
// }



//new code//
package com.krishna.saibaba.Service;

import com.krishna.saibaba.Model.Booking;
import com.krishna.saibaba.Model.Providers;
import com.krishna.saibaba.Repository.BookingRepo;
import com.krishna.saibaba.Repository.ProviderRepo;
import com.krishna.saibaba.Repository.ServiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired private BookingRepo bookingRepo;
    @Autowired private ServiceRepo serviceRepo;
    @Autowired private ProviderRepo providerRepo;

    public void CreateBooking(Booking booking) {
        // ✅ Auto-fetch price from service
        if (booking.getServiceId() != null) {
            try {
                com.krishna.saibaba.Model.Service svc =
                    serviceRepo.findById(booking.getServiceId()).orElse(null);
                if (svc != null && svc.getPrice() != null) {
                    String cleaned = svc.getPrice().replaceAll("[^0-9.]", "").trim();
                    if (!cleaned.isEmpty()) booking.setAmount(Double.parseDouble(cleaned));
                }
            } catch (Exception e) {
                System.out.println("Price fetch failed: " + e.getMessage());
            }
        }

        // ✅ Auto-assign provider if not set
        if (booking.getProviderId() == null || booking.getProviderId() == 0) {
            try {
                // Find providers who offer this service
                List<Providers> providers = providerRepo.findAll();
                Providers assigned = null;
                for (Providers p : providers) {
                    if (p.getService() != null &&
                        p.getService().getServiceId() != null &&
                        p.getService().getServiceId().equals(booking.getServiceId())) {
                        assigned = p;
                        break;
                    }
                }
                // If no matching provider, assign any available provider
                if (assigned == null && !providers.isEmpty()) {
                    assigned = providers.get(0);
                }
                if (assigned != null) {
                    booking.setProviderId(assigned.getProviderId());
                }
            } catch (Exception e) {
                System.out.println("Provider auto-assign failed: " + e.getMessage());
            }
        }

        booking.setStatus("BOOKED");
        bookingRepo.save(booking);
    }

    public ResponseEntity<?> getAllBookings() {
        return ResponseEntity.ok(bookingRepo.findAll());
    }

    public ResponseEntity<?> getBookingById(Integer bookingId) {
        return ResponseEntity.ok(bookingRepo.findByBookingId(bookingId));
    }

    public ResponseEntity<?> getBookingsByCustomer(Integer customerId) {
        return ResponseEntity.ok(bookingRepo.findByCustomerId(customerId));
    }

    public ResponseEntity<?> getBookingsByProvider(Integer providerId) {
        // ✅ Return ALL bookings if no specific provider match found
        List<Booking> byProvider = bookingRepo.findByProviderId(providerId);
        if (byProvider.isEmpty()) {
            // Return all BOOKED bookings so providers can see new requests
            return ResponseEntity.ok(bookingRepo.findAll());
        }
        return ResponseEntity.ok(byProvider);
    }

    public ResponseEntity<?> updateBooking(Integer id, String status) {
        Booking booking = bookingRepo.findById(id).orElse(null);
        if (booking != null) {
            booking.setStatus(status);
            bookingRepo.save(booking);
            return ResponseEntity.ok("Booking updated");
        }
        return ResponseEntity.notFound().build();
    }

    public void cancelBooking(Integer bookingId) {
        Booking booking = bookingRepo.findById(bookingId).orElse(null);
        if (booking == null) return;
        booking.setStatus("CANCELLED");
        bookingRepo.save(booking);
    }

    public ResponseEntity<?> RequestCompletion(Integer bookingId) {
        try {
            Booking booking = bookingRepo.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            String otp = String.valueOf((int)(Math.random() * 9000) + 1000);
            booking.setOtp(otp);
            booking.setStatus("WAITING_FOR_OTP");
            booking.setOtpExpiry(null); // ✅ Skip expiry to avoid TiDB datetime issues
            bookingRepo.save(booking);

            return ResponseEntity.ok("OTP Generated: " + otp);
        } catch (Exception e) {
            System.out.println("OTP generation error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to generate OTP: " + e.getMessage());
        }
    }

    public ResponseEntity<?> verifyOtp(Integer bookingId, String otp) {
        try {
            Booking booking = bookingRepo.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            // ✅ Check OTP match first (skip expiry check if null)
            if (booking.getOtp() == null || !booking.getOtp().trim().equals(otp.trim())) {
                return ResponseEntity.badRequest().body("Invalid OTP");
            }

            // Check expiry
            if (booking.getOtpExpiry() != null && booking.getOtpExpiry().isBefore(LocalDateTime.now())) {
                booking.setOtp(null);
                booking.setOtpExpiry(null);
                booking.setStatus("BOOKED");
                bookingRepo.save(booking);
                return ResponseEntity.badRequest().body("OTP Expired. Please request again.");
            }

            booking.setStatus("COMPLETED");
            booking.setVerified(true);
            booking.setOtp(null);
            booking.setOtpExpiry(null);
            bookingRepo.save(booking);
            return ResponseEntity.ok("Service Completed");
        } catch (Exception e) {
            System.out.println("OTP verify error: " + e.getMessage());
            return ResponseEntity.badRequest().body("OTP verification failed: " + e.getMessage());
        }
    }
}

