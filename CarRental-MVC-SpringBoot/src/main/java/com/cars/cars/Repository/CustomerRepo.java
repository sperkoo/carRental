package com.cars.cars.Repository;

import com.cars.cars.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Integer> {
    List<Customer> findByCustomerUserName(String username);

    @Query("SELECT DISTINCT c FROM Customer c JOIN Message m ON c.customerId = m.userId")
    List<Customer> findCustomersWithMessages();
}