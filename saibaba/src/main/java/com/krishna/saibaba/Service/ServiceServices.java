package com.krishna.saibaba.Service;

import com.krishna.saibaba.Repository.ServiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class ServiceServices {

    @Autowired
    private ServiceRepo serviceRepo;


    public void AddService(com.krishna.saibaba.Model.Service service) {
        serviceRepo.save(service);
    }

    public ResponseEntity<?> GetAllServices() {
       return  ResponseEntity.ok().body(serviceRepo.findAll());
    }

    public ResponseEntity<?> GetServiceById(Integer id) {
        return ResponseEntity.ok().body(serviceRepo.findById(id));
    }
}
