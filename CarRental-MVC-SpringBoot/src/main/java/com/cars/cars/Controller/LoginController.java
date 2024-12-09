package com.cars.cars.Controller;

import com.cars.cars.Model.Customer;
import com.cars.cars.Repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Thymeleaf template name
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // Thymeleaf template name for the registration page
    }

    @PostMapping("/perform_register")
    public String performRegister(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("address") String address,
            @RequestParam("phone") String phone) {

        // Basic validation
        if (!password.equals(confirmPassword)) {
            // Redirect with an error message (can be improved with a model)
            return "redirect:/register?error=Passwords do not match";
        }

        // Create and save the new customer
        Customer customer = new Customer();
        customer.setCustomerUserName(username);
        customer.setCustomerEmail(email);
        customer.setCustomerPassword(passwordEncoder.encode(password)); // Encode the password before saving
        customer.setCustomerFirstName(firstName);
        customer.setCustomerLastName(lastName);
        customer.setCustomerAddress(address);
        customer.setCustomerPhone(phone);

        customerRepo.save(customer);

        return "redirect:/login?success=Registered successfully";
    }
}