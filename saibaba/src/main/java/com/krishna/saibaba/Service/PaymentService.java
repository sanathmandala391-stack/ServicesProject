package com.krishna.saibaba.Service;

import com.krishna.saibaba.Model.Booking;
import com.krishna.saibaba.Model.Payment;
import com.krishna.saibaba.Repository.BookingRepo;
import com.krishna.saibaba.Repository.PaymentRepo;
import com.krishna.saibaba.Repository.ServiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentService {

    @Autowired private PaymentRepo paymentRepo;
    @Autowired private BookingRepo bookingRepo;
    @Autowired private ServiceRepo serviceRepo;
    @Autowired private JavaMailSender mailSender;

    // ✅ Cashfree PRODUCTION credentials
    private static final String CF_APP_ID     = "121304495b973d7ae8b4bba33214403121";
    private static final String CF_SECRET_KEY = "cfsk_ma_prod_de7ed576db7a149176b874e015f30432_a6af54ef";
    private static final String CF_BASE_URL   = "https://api.cashfree.com/pg";  // PRODUCTION
    private static final String CF_API_VER    = "2023-08-01";
    // ════════════════════════════════════════════════════
    // ✅ STEP 1: Paste your ngrok FRONTEND URL here
    //    Run: ngrok http 5173
    //    Copy the https URL e.g: https://abc123.ngrok-free.app
    // ════════════════════════════════════════════════════
    private static final String FRONTEND_URL  = "https://frontend-serve-ease-mtnc.vercel.app";

    // ════════════════════════════════════════════════════
    // ✅ STEP 2: Paste your ngrok BACKEND URL here
    //    Run: ngrok http 8080
    //    Copy the https URL e.g: https://xyz789.ngrok-free.app
    // ════════════════════════════════════════════════════
    private static final String BACKEND_URL   = "https://servicesproject-2.onrender.com";

    @Value("${spring.mail.username}")
    private String fromEmail;

    // ✅ HTTP POST using HttpURLConnection (no RestTemplate needed)
    @SuppressWarnings("unchecked")
    private Map<String, Object> cashfreePost(String endpoint, Map<String, Object> body) throws Exception {
        URL url = new URL(CF_BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("x-client-id", CF_APP_ID);
        conn.setRequestProperty("x-client-secret", CF_SECRET_KEY);
        conn.setRequestProperty("x-api-version", CF_API_VER);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);

        // Simple JSON builder
        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<String, Object> e : body.entrySet()) {
            json.append("\"").append(e.getKey()).append("\":");
            if (e.getValue() instanceof Map) {
                json.append(mapToJson((Map<String, Object>) e.getValue()));
            } else if (e.getValue() instanceof Number) {
                json.append(e.getValue());
            } else {
                json.append("\"").append(e.getValue()).append("\"");
            }
            json.append(",");
        }
        if (json.charAt(json.length()-1) == ',') json.deleteCharAt(json.length()-1);
        json.append("}");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.toString().getBytes(StandardCharsets.UTF_8));
        }

        int code = conn.getResponseCode();
        InputStream is = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
        StringBuilder resp = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) resp.append(line);
        }

        System.out.println("Cashfree [" + code + "]: " + resp);
        Map<String, Object> result = new HashMap<>();
        result.put("raw", resp.toString());
        result.put("status_code", code);

        // Extract payment_session_id
        String sessionId = extractValue(resp.toString(), "payment_session_id");
        if (sessionId != null) result.put("payment_session_id", sessionId);

        // Extract cf_order_id
        String cfOrderId = extractValue(resp.toString(), "cf_order_id");
        if (cfOrderId != null) result.put("cf_order_id", cfOrderId);

        // Extract order_status
        String orderStatus = extractValue(resp.toString(), "order_status");
        if (orderStatus != null) result.put("order_status", orderStatus);

        result.put("success", code >= 200 && code < 300 && sessionId != null);
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> cashfreeGet(String endpoint) throws Exception {
        URL url = new URL(CF_BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("x-client-id", CF_APP_ID);
        conn.setRequestProperty("x-client-secret", CF_SECRET_KEY);
        conn.setRequestProperty("x-api-version", CF_API_VER);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        int code = conn.getResponseCode();
        InputStream is = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
        StringBuilder resp = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) resp.append(line);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("raw", resp.toString());
        String orderStatus = extractValue(resp.toString(), "order_status");
        if (orderStatus != null) result.put("order_status", orderStatus);
        result.put("success", code >= 200 && code < 300);
        return result;
    }

    private String mapToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<String, Object> e : map.entrySet()) {
            sb.append("\"").append(e.getKey()).append("\":\"").append(e.getValue()).append("\",");
        }
        if (sb.charAt(sb.length()-1) == ',') sb.deleteCharAt(sb.length()-1);
        sb.append("}");
        return sb.toString();
    }

    private String extractValue(String json, String key) {
        try {
            String[] patterns = {
                "\"" + key + "\":\"", "\"" + key + "\": \"",
                "\"" + key + "\":\""
            };
            for (String pattern : patterns) {
                int idx = json.indexOf(pattern);
                if (idx >= 0) {
                    int start = idx + pattern.length();
                    int end = json.indexOf("\"", start);
                    if (end > start) return json.substring(start, end);
                }
            }
        } catch (Exception e) { /* ignore */ }
        return null;
    }

    // ✅ Get actual service price
    private Double getServicePrice(Integer serviceId) {
        if (serviceId == null) return null;
        try {
            com.krishna.saibaba.Model.Service svc = serviceRepo.findById(serviceId).orElse(null);
            if (svc != null && svc.getPrice() != null && !svc.getPrice().isEmpty()) {
                String cleaned = svc.getPrice().replaceAll("[^0-9.]", "").trim();
                if (!cleaned.isEmpty()) {
                    double price = Double.parseDouble(cleaned);
                    if (price > 0) return price;
                }
            }
        } catch (Exception e) {
            System.out.println("Price error: " + e.getMessage());
        }
        return null;
    }

    // ✅ PRE-BOOKING ORDER — Flipkart style: pay first then booking created
    public ResponseEntity<?> createPreBookingOrder(Integer serviceId, String customerEmail,
                                                    String customerName, String customerPhone,
                                                    Integer customerId) {
        Double amount = getServicePrice(serviceId);
        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest()
                .body("Service price not set. Please update via admin panel.");
        }

        String phone = customerPhone.replaceAll("[^0-9]", "");
        if (phone.length() > 10) phone = phone.substring(phone.length() - 10);
        if (phone.length() < 10) phone = "9999999999";

        String orderId = "SRV" + serviceId + "C" + customerId + "T" + System.currentTimeMillis();

        try {
            Map<String, Object> customerDetails = new HashMap<>();
            customerDetails.put("customer_id", "CUST" + customerId);
            customerDetails.put("customer_email", customerEmail != null ? customerEmail : "customer@serveease.com");
            customerDetails.put("customer_phone", phone);

            Map<String, Object> orderMeta = new HashMap<>();
            orderMeta.put("return_url",
                FRONTEND_URL + "/home?payment_status={order_status}&order_id=" + orderId
                + "&service_id=" + serviceId);
            orderMeta.put("notify_url", BACKEND_URL + "/payment/cashfree-webhook");

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("order_id", orderId);
            body.put("order_amount", amount);
            body.put("order_currency", "INR");
            body.put("customer_details", customerDetails);
            body.put("order_meta", orderMeta);

            Map<String, Object> response = cashfreePost("/orders", body);

            if (!Boolean.TRUE.equals(response.get("success"))) {
                System.out.println("Cashfree order failed: " + response.get("raw"));
                return ResponseEntity.status(500)
                    .body("Payment setup failed: " + response.get("raw"));
            }

            Map<String, Object> result = new HashMap<>();
            result.put("paymentSessionId", response.get("payment_session_id"));
            result.put("orderId", orderId);
            result.put("amount", amount);

            System.out.println("Cashfree order created: " + orderId + " | ₹" + amount);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.out.println("Cashfree exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Payment error: " + e.getMessage());
        }
    }

    // ✅ EXISTING BOOKING PAYMENT (from MyBookings)
    public ResponseEntity<?> initiatePayment(Integer bookingId, Double amountIgnored,
                                              String paymentType, String customerEmail,
                                              String customerName, String customerPhone) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        List<Payment> existing = paymentRepo.findByBookingId(bookingId);
        for (Payment p : existing) {
            if ("SUCCESS".equals(p.getStatus()) || "COD".equals(p.getStatus())) {
                return ResponseEntity.badRequest().body("Payment already completed");
            }
        }

        Double amount = getServicePrice(booking.getServiceId());
        if (amount == null || amount <= 0) amount = booking.getAmount();
        if (amount == null || amount <= 0) amount = 500.0;

        booking.setAmount(amount);
        bookingRepo.save(booking);

        String phone = customerPhone.replaceAll("[^0-9]", "");
        if (phone.length() > 10) phone = phone.substring(phone.length() - 10);
        if (phone.length() < 10) phone = "9999999999";

        String orderId = "BKG" + bookingId + "T" + System.currentTimeMillis();

        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setCustomerId(booking.getCustomerId());
        payment.setAmount(amount);
        payment.setPaymentType(paymentType);
        payment.setTxnId(orderId);
        payment.setStatus("PENDING");
        paymentRepo.save(payment);

        try {
            Map<String, Object> customerDetails = new HashMap<>();
            customerDetails.put("customer_id", "CUST" + booking.getCustomerId());
            customerDetails.put("customer_email", customerEmail != null ? customerEmail : "customer@serveease.com");
            customerDetails.put("customer_phone", phone);

            Map<String, Object> orderMeta = new HashMap<>();
            orderMeta.put("return_url",
                FRONTEND_URL + "/mybookings?payment_status={order_status}&order_id=" + orderId);
            orderMeta.put("notify_url", BACKEND_URL + "/payment/cashfree-webhook");

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("order_id", orderId);
            body.put("order_amount", amount);
            body.put("order_currency", "INR");
            body.put("customer_details", customerDetails);
            body.put("order_meta", orderMeta);

            Map<String, Object> response = cashfreePost("/orders", body);

            if (!Boolean.TRUE.equals(response.get("success"))) {
                payment.setStatus("FAILED");
                paymentRepo.save(payment);
                return ResponseEntity.status(500).body("Cashfree error: " + response.get("raw"));
            }

            payment.setPayuTxnId((String) response.get("cf_order_id"));
            paymentRepo.save(payment);

            Map<String, Object> result = new HashMap<>();
            result.put("paymentSessionId", response.get("payment_session_id"));
            result.put("orderId", orderId);
            result.put("amount", amount);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            payment.setStatus("FAILED");
            paymentRepo.save(payment);
            return ResponseEntity.status(500).body("Payment error: " + e.getMessage());
        }
    }

    // ✅ Cashfree webhook
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> handleCashfreeWebhook(Map<String, Object> payload) {
        try {
            System.out.println("Cashfree Webhook: " + payload);
            Object dataObj = payload.get("data");
            if (dataObj == null) return ResponseEntity.ok("OK");

            Map<String, Object> data  = (Map<String, Object>) dataObj;
            Map<String, Object> order = (Map<String, Object>) data.get("order");
            if (order == null) return ResponseEntity.ok("OK");

            String orderId     = (String) order.get("order_id");
            String orderStatus = (String) order.get("order_status");

            Payment payment = paymentRepo.findByTxnId(orderId).orElse(null);
            if (payment == null) return ResponseEntity.ok("OK");

            if ("PAID".equalsIgnoreCase(orderStatus)) {
                payment.setStatus("SUCCESS");
                paymentRepo.save(payment);
                Booking booking = bookingRepo.findById(payment.getBookingId()).orElse(null);
                if (booking != null) {
                    booking.setPaymentStatus("SUCCESS");
                    bookingRepo.save(booking);
                    sendPaymentEmail(payment);
                }
            } else if ("EXPIRED".equalsIgnoreCase(orderStatus) || "CANCELLED".equalsIgnoreCase(orderStatus)) {
                payment.setStatus("FAILED");
                paymentRepo.save(payment);
            }
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }

    // ✅ Verify payment after redirect
    public ResponseEntity<?> verifyPayment(String orderId) {
        try {
            Payment payment = paymentRepo.findByTxnId(orderId).orElse(null);
            if (payment == null) return ResponseEntity.badRequest().body("Not found");

            if ("SUCCESS".equals(payment.getStatus())) {
                Map<String, Object> result = new HashMap<>();
                result.put("status", "SUCCESS");
                result.put("amount", payment.getAmount());
                result.put("bookingId", payment.getBookingId());
                return ResponseEntity.ok(result);
            }

            // Double-check with Cashfree
            try {
                Map<String, Object> cfResp = cashfreeGet("/orders/" + orderId);
                String cfStatus = (String) cfResp.get("order_status");
                if ("PAID".equalsIgnoreCase(cfStatus)) {
                    payment.setStatus("SUCCESS");
                    paymentRepo.save(payment);
                    Booking booking = bookingRepo.findById(payment.getBookingId()).orElse(null);
                    if (booking != null) {
                        booking.setPaymentStatus("SUCCESS");
                        bookingRepo.save(booking);
                        sendPaymentEmail(payment);
                    }
                    Map<String, Object> result = new HashMap<>();
                    result.put("status", "SUCCESS");
                    result.put("amount", payment.getAmount());
                    result.put("bookingId", payment.getBookingId());
                    return ResponseEntity.ok(result);
                }
            } catch (Exception ve) {
                System.out.println("CF verify error: " + ve.getMessage());
            }

            Map<String, Object> result = new HashMap<>();
            result.put("status", payment.getStatus());
            result.put("amount", payment.getAmount());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Verification failed");
        }
    }

    // ✅ COD
    public ResponseEntity<?> confirmCashOnDelivery(Integer bookingId) {
        try {
            Booking booking = bookingRepo.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            List<Payment> existing = paymentRepo.findByBookingId(bookingId);
            for (Payment p : existing) {
                if ("COD".equals(p.getPaymentType()) || "SUCCESS".equals(p.getStatus())) {
                    return ResponseEntity.ok("Already confirmed");
                }
            }

            Double amount = getServicePrice(booking.getServiceId());
            if (amount == null || amount <= 0) amount = booking.getAmount();
            if (amount == null || amount <= 0) amount = 500.0;

            booking.setAmount(amount);
            booking.setPaymentStatus("COD");
            bookingRepo.save(booking);

            Payment payment = new Payment();
            payment.setBookingId(bookingId);
            payment.setCustomerId(booking.getCustomerId());
            payment.setAmount(amount);
            payment.setPaymentType("COD");
            payment.setStatus("COD");
            payment.setTxnId("COD" + System.currentTimeMillis());
            paymentRepo.save(payment);

            return ResponseEntity.ok("COD confirmed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("COD failed: " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 60000)
    public void clearExpiredOtps() {
        List<Booking> all = bookingRepo.findAll();
        for (Booking b : all) {
            if (b.getStatus() != null
                    && b.getStatus().equalsIgnoreCase("WAITING_FOR_OTP")
                    && b.getOtpExpiry() != null
                    && b.getOtpExpiry().isBefore(LocalDateTime.now())) {
                b.setOtp(null);
                b.setOtpExpiry(null);
                b.setStatus("BOOKED");
                bookingRepo.save(b);
            }
        }
    }

    private void sendPaymentEmail(Payment payment) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(fromEmail);
            msg.setSubject("Payment Confirmed — ServeEase #" + payment.getBookingId());
            msg.setText("Payment confirmed!\nBooking: #" + payment.getBookingId()
                + "\nAmount: Rs." + payment.getAmount()
                + "\nOrder: " + payment.getTxnId());
            mailSender.send(msg);
        } catch (Exception e) {
            System.out.println("Email error: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getPaymentsByCustomer(Integer customerId) { return ResponseEntity.ok(paymentRepo.findByCustomerId(customerId)); }
    public ResponseEntity<?> getPaymentsByBooking(Integer bookingId)   { return ResponseEntity.ok(paymentRepo.findByBookingId(bookingId)); }
    public ResponseEntity<?> getAllPayments()                           { return ResponseEntity.ok(paymentRepo.findAll()); }
}
