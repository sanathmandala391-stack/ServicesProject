package com.krishna.saibaba.Controller;

import com.krishna.saibaba.Model.LocationMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class LocationController {

    // ✅ Provider sends to /app/send-location
    // ✅ Customer receives from /topic/location/{bookingId}
    @MessageMapping("/send-location")
    @SendTo("/topic/location")
    public LocationMessage shareLocation(LocationMessage message) {
        System.out.println("Provider " + message.getProviderId() +
                " location: " + message.getLatitude() + ", " + message.getLongitude());
        return message;
    }
}