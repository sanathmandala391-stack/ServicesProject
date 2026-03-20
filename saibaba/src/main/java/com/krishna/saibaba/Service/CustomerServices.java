package com.krishna.saibaba.Service;

import com.krishna.saibaba.Config.JWTService;
import com.krishna.saibaba.Model.Customers;
import com.krishna.saibaba.Repository.CustomersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomerServices {

    @Autowired
    private CustomersRepo cusRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtService;

    public void registerCustomer(Customers cus) {
        cus.setEmail(cus.getEmail().toLowerCase());
        cus.setPassword(passwordEncoder.encode(cus.getPassword()));
        cusRepo.save(cus);
    }

    // Keep old method name for backward compatibility
    public void CustomerRegister(Customers cus) {
        registerCustomer(cus);
    }

    public ResponseEntity<?> loginCustomer(Customers customer) {
        return CustomerLogin(customer.getEmail(), customer.getPassword());
    }

    public ResponseEntity<?> CustomerLogin(String email, String password) {
        Customers customers = cusRepo.findByEmail(email.toLowerCase());

        if (customers == null) {
            return ResponseEntity.badRequest().body("Email not found");
        }

        boolean valid = passwordEncoder.matches(password, customers.getPassword());

        if (!valid) {
            return ResponseEntity.badRequest().body("Invalid password");
        }

        String token = jwtService.generateToken(customers.getEmail());

        return ResponseEntity.ok(Map.of(
                "message", "Login Successful",
                "token", token,
                "name", customers.getName(),
                "customerId", customers.getCustomerId(),
                "email", customers.getEmail(),
                "phone", customers.getPhone() != null ? customers.getPhone() : ""
        ));
    }

    // ✅ Admin endpoint
    public ResponseEntity<?> getAllCustomers() {
        return ResponseEntity.ok(cusRepo.findAll());
    }

    public ResponseEntity<?> getCustomerById(Integer customerId) {
        return ResponseEntity.ok(cusRepo.findById(customerId).orElse(null));
    }
}
