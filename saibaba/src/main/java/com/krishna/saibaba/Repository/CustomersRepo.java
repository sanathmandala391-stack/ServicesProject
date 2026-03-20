package com.krishna.saibaba.Repository;

import com.krishna.saibaba.Model.Customers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomersRepo extends JpaRepository<Customers,Integer> {

    Customers findByEmail(String email);
}
