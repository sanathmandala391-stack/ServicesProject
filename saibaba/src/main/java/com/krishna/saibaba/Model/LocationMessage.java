package com.krishna.saibaba.Model;

public class LocationMessage {
    private Integer providerId;
    private Integer bookingId;
    private double latitude;
    private double longitude;

    public LocationMessage() {}

    public LocationMessage(Integer providerId, Integer bookingId, double latitude, double longitude) {
        this.providerId = providerId;
        this.bookingId = bookingId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getProviderId() { return providerId; }
    public void setProviderId(Integer providerId) { this.providerId = providerId; }

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}