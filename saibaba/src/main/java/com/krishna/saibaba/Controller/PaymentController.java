package com.krishna.saibaba.Controller;

import com.krishna.saibaba.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // ✅ Flipkart style: pay BEFORE booking is created
    @PostMapping("/pre-order")
    public ResponseEntity<?> preOrder(@RequestBody Map<String, Object> body) {
        Integer serviceId  = Integer.parseInt(body.get("serviceId").toString());
        Integer customerId = Integer.parseInt(body.get("customerId").toString());
        String email  = body.getOrDefault("email",  "customer@serveease.com").toString();
        String name   = body.getOrDefault("name",   "Customer").toString();
        String phone  = body.getOrDefault("phone",  "9999999999").toString();
        return paymentService.createPreBookingOrder(serviceId, email, name, phone, customerId);
    }

    // ✅ Payment for existing booking
    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@RequestBody Map<String, Object> body) {
        Integer bookingId  = Integer.parseInt(body.get("bookingId").toString());
        Double amount      = Double.parseDouble(body.get("amount").toString());
        String paymentType = body.get("paymentType").toString();
        String email  = body.getOrDefault("email",  "customer@serveease.com").toString();
        String name   = body.getOrDefault("name",   "Customer").toString();
        String phone  = body.getOrDefault("phone",  "9999999999").toString();
        return paymentService.initiatePayment(bookingId, amount, paymentType, email, name, phone);
    }

    // ✅ Cashfree webhook (called by Cashfree after payment)
    @PostMapping("/cashfree-webhook")
    public ResponseEntity<?> cashfreeWebhook(@RequestBody Map<String, Object> payload) {
        return paymentService.handleCashfreeWebhook(payload);
    }

    // ✅ Verify payment after redirect
    @GetMapping("/verify/{orderId}")
    public ResponseEntity<?> verifyPayment(@PathVariable String orderId) {
        return paymentService.verifyPayment(orderId);
    }

    // ✅ Cash on Delivery
    @PostMapping("/cash/{bookingId}")
    public ResponseEntity<?> cashOnDelivery(@PathVariable Integer bookingId) {
        return paymentService.confirmCashOnDelivery(bookingId);
    }

    // Admin
    @GetMapping("/all")
    public ResponseEntity<?> getAllPayments() { return paymentService.getAllPayments(); }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getByCustomer(@PathVariable Integer customerId) { return paymentService.getPaymentsByCustomer(customerId); }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getByBooking(@PathVariable Integer bookingId) { return paymentService.getPaymentsByBooking(bookingId); }
}
