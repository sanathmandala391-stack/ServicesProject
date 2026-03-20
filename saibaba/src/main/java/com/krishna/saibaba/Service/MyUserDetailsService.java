package com.krishna.saibaba.Service;

import com.krishna.saibaba.Model.Customers;
import com.krishna.saibaba.Repository.CustomersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {


    @Autowired
    CustomersRepo customersRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customers customers = customersRepo.findByEmail(username);


        if (customers == null) {
            throw new UsernameNotFoundException("Customer not found");

        }
        return User
                .withUsername(customers.getEmail())
                .password(customers.getPassword())
                .roles("Customer")
                .build();

    }
}










