package com.krishna.saibaba.Repository;

import com.krishna.saibaba.Model.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepo extends JpaRepository<Service,Integer> {
    Object findByServiceId(Integer serviceId);
}
