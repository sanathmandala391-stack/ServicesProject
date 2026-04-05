package com.krishna.saibaba.Service;

import com.krishna.saibaba.Repository.ServiceRepo;
import com.krishna.saibaba.Model.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.util.List;

@org.springframework.stereotype.Service
public class ServiceServices {

    @Autowired
    private ServiceRepo serviceRepo;

    public void AddService(Service service) {
        serviceRepo.save(service);
    }

    public List<Service> saveAll(List<Service> services) {
        return serviceRepo.saveAll(services); // ✅ FIXED
    }

    public ResponseEntity<?> GetAllServices() {
        return ResponseEntity.ok().body(serviceRepo.findAll());
    }

    public ResponseEntity<?> GetServiceById(Integer id) {
        return ResponseEntity.ok().body(serviceRepo.findById(id));
    }
}
