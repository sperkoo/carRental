package com.cars.cars.security;

import com.cars.cars.Model.Customer;
import com.cars.cars.Repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepo customerRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<Customer> customers = customerRepo.findByCustomerUserName(username);
        if (customers.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        // Assuming the first customer in the list is the one to authenticate
        Customer customer = customers.get(0);
        return new CustomUserDetails(customer);
    }
}