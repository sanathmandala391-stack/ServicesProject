package com.krishna.saibaba.Controller;

import com.krishna.saibaba.Model.Booking;
import com.krishna.saibaba.Service.BookingService;
import jakarta.persistence.GeneratedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import java.util.Map;

@RestController
public class BookingController {
   @Autowired
    private BookingService bookingService;

   @PostMapping("/add-booking")
   public ResponseEntity<?> CreateBooking(@RequestBody Booking booking){
  try {
      bookingService.CreateBooking(booking);
      return ResponseEntity.ok().body("Service Booked Sucessfully.");
  }
  catch (Exception e){
     return ResponseEntity.badRequest().body("Failed To Book Service.");
  }
   }

   //For get all Bookings//
   @GetMapping("/getAll-Bookings")
   public ResponseEntity<?> getAllBookings(){
      return bookingService.getAllBookings();
   }

   //for get Bookings by bookingbyId//

    @GetMapping("/getBooking/{BookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable Integer BookingId){
      return bookingService.getBookingById(BookingId);
    }

    @GetMapping("/getBookings/customer/{customerId}")
    public ResponseEntity<?> getBookingsByCustomer(@PathVariable Integer customerId){
     return bookingService.getBookingsByCustomer(customerId);
    }

    @GetMapping("/getBookings/provider/{providerId}")
    public ResponseEntity<?> getBookingsByProvider(@PathVariable Integer providerId){
       return bookingService.getBookingsByProvider(providerId);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Integer id,@RequestBody Map<String ,String > body){
       String status=body.get("status");
    return bookingService.updateBooking(id,status);
    }


    @PutMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Integer bookingId ){

       bookingService.cancelBooking(bookingId);
       return ResponseEntity.ok().body("Booking Canceled");
    }


    @PutMapping("/request-completion/{bookingId}")
    public ResponseEntity<?> RequestCompletion(@PathVariable Integer bookingId){
       return bookingService.RequestCompletion(bookingId);
    }

    @PutMapping("/verify-completion/{bookingId}/{otp}")
    public ResponseEntity<?> verifyOtp(@PathVariable Integer bookingId,@PathVariable String otp){
       return bookingService.verifyOtp(bookingId,otp);
    }


}



// package com.krishna.saibaba.Controller;

// import com.krishna.saibaba.Model.Booking;
// import com.krishna.saibaba.Service.BookingService;
// import jakarta.persistence.GeneratedValue;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import javax.print.DocFlavor;
// import java.util.Map;

// @RestController
// public class BookingController {
//     @Autowired
//     private BookingService bookingService;

//     @PostMapping("/add-booking")
//     public ResponseEntity<?> CreateBooking(@RequestBody Booking booking){
//         try {
//             bookingService.CreateBooking(booking);
//             return ResponseEntity.ok().body("Service Booked Sucessfully.");
//         }
//         catch (Exception e){
//             return ResponseEntity.badRequest().body("Failed To Book Service.");
//         }
//     }

//     //For get all Bookings//
//     @GetMapping("/getAll-Bookings")
//     public ResponseEntity<?> getAllBookings(){
//         return bookingService.getAllBookings();
//     }

//     //for get Bookings by bookingbyId//

//     @GetMapping("/getBooking/{BookingId}")
//     public ResponseEntity<?> getBookingById(@PathVariable Integer BookingId){
//         return bookingService.getBookingById(BookingId);
//     }

//     @GetMapping("/getBookings/customer/{customerId}")
//     public ResponseEntity<?> getBookingsByCustomer(@PathVariable Integer customerId){
//         return bookingService.getBookingsByCustomer(customerId);
//     }

//     @GetMapping("/getBookings/provider/{providerId}")
//     public ResponseEntity<?> getBookingsByProvider(@PathVariable Integer providerId){
//         return bookingService.getBookingsByProvider(providerId);
//     }

//     @PutMapping("/update/{id}")
//     public ResponseEntity<?> updateBooking(@PathVariable Integer id,@RequestBody Map<String,String> body){
//         String status = body.get("status");
//         String providerIdStr = body.get("providerId");
//         Integer providerId = null;
//         if (providerIdStr != null && !providerIdStr.isEmpty()) {
//             try { providerId = Integer.parseInt(providerIdStr); } catch (Exception e) {}
//         }
//         return bookingService.updateBooking(id, status, providerId);
//     }


//     @PutMapping("/cancel/{bookingId}")
//     public ResponseEntity<?> cancelBooking(@PathVariable Integer bookingId ){

//         bookingService.cancelBooking(bookingId);
//         return ResponseEntity.ok().body("Booking Canceled");
//     }


//     @PutMapping("/request-completion/{bookingId}")
//     public ResponseEntity<?> RequestCompletion(@PathVariable Integer bookingId){
//         return bookingService.RequestCompletion(bookingId);
//     }

//     @PutMapping("/verify-completion/{bookingId}/{otp}")
//     public ResponseEntity<?> verifyOtp(@PathVariable Integer bookingId,@PathVariable String otp){
//         return bookingService.verifyOtp(bookingId,otp);
//     }



