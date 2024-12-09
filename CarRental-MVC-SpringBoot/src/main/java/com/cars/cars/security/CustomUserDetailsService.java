package com.cars.cars.security;

import com.cars.cars.Model.Customer;
import com.cars.cars.Repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepo customerRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepo.findByCustomerUserName(username);
        if (customer == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(customer);
    }
}