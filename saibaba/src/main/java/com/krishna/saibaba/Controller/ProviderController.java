package com.krishna.saibaba.Controller;

import com.krishna.saibaba.Model.Providers;
import com.krishna.saibaba.Service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ProviderController {

    @Autowired
    private ProviderService providerService;

    // ✅ Fixed: /add-Provider
    @PostMapping("/add-Provider")
    public ResponseEntity<String> ProviderRegister(@RequestBody Providers providers) {
        try {
            providerService.ProviderRegister(providers);
            return ResponseEntity.ok("Provider Registered Successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to Register Provider: " + e.getMessage());
        }
    }

    // ✅ Fixed: return directly — no double wrapping
    @PostMapping("/Login-Provider")
    public ResponseEntity<?> ProviderLogin(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String password = body.get("password");
            // ✅ Call directly — ProviderService returns ResponseEntity already
            return providerService.ProviderLogin(email, password);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }

    @GetMapping("/getAllProviders")
    public ResponseEntity<?> GetAllProviders() {
        return ResponseEntity.ok(providerService.GetAllProviders());
    }

    @GetMapping("/getProvider/{id}")
    public ResponseEntity<?> GetProviderById(@PathVariable Integer id) {
        return ResponseEntity.ok(providerService.GetProviderById(id));
    }

    @GetMapping("/getProvider/service/{serviceId}")
    public ResponseEntity<?> GetProviderByServiceId(@PathVariable Integer serviceId) {
        return ResponseEntity.ok(providerService.GetProviderByServiceId(serviceId));
    }
}
