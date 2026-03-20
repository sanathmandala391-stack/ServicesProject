package com.krishna.saibaba.Controller;

import com.krishna.saibaba.Model.Customers;
import com.krishna.saibaba.Service.CustomerServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerServices customerServices;

    @PostMapping("/customer-Register")
    public ResponseEntity<?> registerCustomer(@RequestBody Customers customer) {
        try {
            customerServices.registerCustomer(customer);
            return ResponseEntity.ok("Customer Registered Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/customer-Login")
    public ResponseEntity<?> loginCustomer(@RequestBody Customers customer) {
        return customerServices.loginCustomer(customer);
    }

    // ✅ Admin endpoint — get all customers
    @GetMapping("/getAllCustomers")
    public ResponseEntity<?> getAllCustomers() {
        return customerServices.getAllCustomers();
    }

    @GetMapping("/getCustomer/{customerId}")
    public ResponseEntity<?> getCustomer(@PathVariable Integer customerId) {
        return customerServices.getCustomerById(customerId);
    }
}
