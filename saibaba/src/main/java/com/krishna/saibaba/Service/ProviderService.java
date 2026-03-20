package com.krishna.saibaba.Service;

import com.krishna.saibaba.Config.JWTService;
import com.krishna.saibaba.Model.Providers;
import com.krishna.saibaba.Repository.ProviderRepo;
import com.krishna.saibaba.Repository.ServiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Service
public class ProviderService {

    @Autowired
    private ProviderRepo providerRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtService;

    @Autowired
    ServiceRepo serviceRepo;

    public void ProviderRegister(Providers providers) {
        providers.setPassword(passwordEncoder.encode(providers.getPassword()));
        providerRepo.save(providers);
    }


    public ResponseEntity<Map<String, Object>> ProviderLogin(String email, String password) {
        Providers providers=providerRepo.findByEmail(email.toLowerCase());

        if(providers==null){
            throw new RuntimeException("Email Not Found.");
        }
        String token=jwtService.generateToken(providers.getEmail());

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Login Successful");
        response.put("token", token);
        response.put("providerName", providers.getName());
        response.put("providerId", providers.getProviderId());

        return ResponseEntity.ok(response);
    }


    public Object GetAllProviders() {
       return providerRepo.findAll();
    }

    public Object GetProviderById(Integer id) {
        return providerRepo.findById(id);
    }


    public Object GetProviderByServiceId(Integer serviceId) {
       return serviceRepo.findByServiceId(serviceId);
    }
}
