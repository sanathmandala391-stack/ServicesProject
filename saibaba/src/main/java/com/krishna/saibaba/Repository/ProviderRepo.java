package com.krishna.saibaba.Repository;

import com.krishna.saibaba.Model.Providers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepo extends JpaRepository<Providers,Integer> {
    Providers findByEmail(String lowerCase);


}
