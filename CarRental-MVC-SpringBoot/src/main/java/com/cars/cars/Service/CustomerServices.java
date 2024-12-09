package com.cars.cars.Service;

import com.cars.cars.Model.Customer;

import java.util.List;

public interface CustomerServices {

    List<Customer> GetAllCustomer();
    Customer FindCustomerById(int customerId);
    Customer findByCustomerUserName(String username); // Add this method
    void SaveCustomer(Customer customer);
    void DeleteCustomer(Customer customer);

}