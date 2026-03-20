package com.krishna.saibaba.Model;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Customers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer customerId;

    String name;
    @JoinColumn(unique = true)
    String email;
    String password;
    @JoinColumn(unique = true)
    String phone;
    String address;
    LocalDate createdAt;

    public Customers(){

    }

    @PrePersist
    public void setDefaultValues(){
        this.createdAt = LocalDate.now();
    }

    public Customers(Integer customerId, String name, String email, String password, String phone, String address, LocalDate createdAt) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.createdAt = createdAt;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
