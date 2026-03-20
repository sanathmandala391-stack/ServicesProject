package com.krishna.saibaba.Controller;

import com.krishna.saibaba.Model.Booking;
import com.krishna.saibaba.Repository.BookingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class RatingController {

    @Autowired
    private BookingRepo bookingRepo;

    @PostMapping("/rating/submit")
    public ResponseEntity<?> submitRating(@RequestBody Map<String, Object> body) {
        Integer bookingId = Integer.parseInt(body.get("bookingId").toString());
        Integer rating = Integer.parseInt(body.get("rating").toString());
        String review = body.get("review") != null ? body.get("review").toString() : "";

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setRating(rating);
        booking.setReview(review);
        bookingRepo.save(booking);

        return ResponseEntity.ok("Rating submitted");
    }
}