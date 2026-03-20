package com.krishna.saibaba.Model;

import jakarta.persistence.*;

@Entity
public class Providers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer providerId;
    String name;
    @JoinColumn(unique = true)
    String email;
    String password;
    @JoinColumn(unique = true)
    String phone;
    String experience;
    String location;
    @ManyToOne
    Service service;
    String rating;

  public Providers(){

  }

    public Providers(Integer providerId, String name, String email, String password, String phone, String experience, String location, Service service, String rating) {
        this.providerId = providerId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.experience = experience;
        this.location = location;
        this.service = service;
        this.rating = rating;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
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

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
