package com.cars.cars.Controller;

import com.cars.cars.Model.Booking;
import com.cars.cars.Model.Car;
import com.cars.cars.Model.Customer;
import com.cars.cars.Model.Message;
import com.cars.cars.Service.BookingService;
import com.cars.cars.Service.CarService;
import com.cars.cars.Service.CustomerService;
import com.cars.cars.Service.MessageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.UUID;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Controller
public class AdminContoller {

    private static final Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private CarService carService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BookingService bookingService;

    @PostMapping("/generate-contract")
    public ResponseEntity<InputStreamResource> generateContract(@RequestParam Integer bookingId) {
        Booking booking = bookingService.FindBooking(bookingId);
        Car car = carService.findCarById(booking.getCarId());
        Customer customer = customerService.FindCustomerById(booking.getCustomerId());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("Booking Contract"));
            document.add(new Paragraph("Customer: " + customer.getCustomerFirstName() + " " + customer.getCustomerLastName()));
            document.add(new Paragraph("Car: " + car.getCarName() + " " + car.getCarModel()));
            document.add(new Paragraph("Booking Date From: " + booking.getBookingDateFrom()));
            document.add(new Paragraph("Booking Date To: " + booking.getBookingDateTo()));
            document.add(new Paragraph("Total Price: " + booking.getTotalPrice()));
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=contract.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    // AdminController.java
@PostMapping("/confirm-booking")
public String confirmBooking(@RequestParam Integer bookingId) {
    Booking booking = bookingService.FindBooking(bookingId);
    booking.setStatus("Reserved");
    bookingService.BookingSave(booking);

    Car car = carService.findCarById(booking.getCarId());
    car.setCarStatus("Reserved");
    carService.SaveCar(car);

    logger.info("Admin confirmed booking with ID: " + bookingId + " and car status set to Reserved");
    return "redirect:/admin/booking-requests";
}

@PostMapping("/reject-booking")
public String rejectBooking(@RequestParam Integer bookingId) {
    Booking booking = bookingService.FindBooking(bookingId);
    Car car = carService.findCarById(booking.getCarId());
    car.setCarStatus("Available"); // Set status to Available
    carService.SaveCar(car);

    bookingService.DeleteBooking(bookingId);
    logger.info("Admin rejected booking with ID: " + bookingId + " and car status set to Available");
    return "redirect:/admin/booking-requests";
}

    @GetMapping("/admin/vehicles")
    public ModelAndView GetAllCarsForAdmin(){
        ModelAndView modelAndView = new ModelAndView("admin-cars");
        List<Car> carList = carService.GetAllCars();
        modelAndView.addObject("carList",carList);
        logger.info("Admin viewing cars");
        return modelAndView;
    }

    // AdminController.java
@GetMapping("/admin/booking-requests")
public ModelAndView getBookingRequests() {
    ModelAndView modelAndView = new ModelAndView("booking-requests");
    List<Booking> bookingRequests = bookingService.findAllByStatus("Pending");
    modelAndView.addObject("bookingRequests", bookingRequests);
    logger.info("Admin viewing booking requests");
    return modelAndView;
}
    @GetMapping("/admin/add-vehicle")
    public ModelAndView AddCar(){
        ModelAndView modelAndView = new ModelAndView("new-car");
        Car car = new Car();
        modelAndView.addObject("car",car);
        logger.info("Admin trying to add a new car");
        return modelAndView;
    }

    @GetMapping("/admin/vehicle")
    public ModelAndView UpdateCarInfo(@RequestParam Integer carId){
        ModelAndView modelAndView = new ModelAndView("update-car");
        Car car = carService.findCarById(carId);
        modelAndView.addObject("car",car);
        logger.info("Admin trying to update a car");
        return modelAndView;
    }


    @GetMapping("error-delete")
    public ModelAndView DeleteError(){
        logger.info("Admin couldn't delete a car");
        return new ModelAndView("error-delete");
    }

    @GetMapping("delete-cars")
    public String DeleteCar(@RequestParam Integer carId){
        Car car = carService.findCarById(carId);
        if("Available".equals(car.getCarStatus())){
            carService.DeleteCar(carId);
            logger.info("Admin deleted a car with info " + " " + car.getCarName() + " " + car.getCarModel());
            return "redirect:/admin/vehicles";
        }
        else {
            return "redirect:/error-delete";
        }

    }
    @GetMapping("/admin/customers")
    public ModelAndView GetAllCustomers(){
        ModelAndView modelAndView = new ModelAndView("customers");
        List<Customer> customerList = customerService.GetAllCustomer();
        modelAndView.addObject("customerList",customerList);
        logger.info("Admin viewing customers");
        return modelAndView;
    }

    @GetMapping("/admin/customer/customer-id")
    public ModelAndView UpdateCustomer(@RequestParam int customerId){
        ModelAndView modelAndView = new ModelAndView("update-customer");
        Customer customer = customerService.FindCustomerById(customerId);
        modelAndView.addObject("customer",customer);
        logger.info("Admin trying to update a customer info");
        return modelAndView;
    }

    @GetMapping("delete/customer")
    public String DeleteCustomer(@RequestParam Integer customerId){
        Customer customer = customerService.FindCustomerById(customerId);
        customerService.DeleteCustomer(customer);
        logger.info("Admin deleted a customer");
        return "redirect:/admin/customers";
    }

    @GetMapping("/admin/new/customer")
    public ModelAndView AddCustomer(){
        ModelAndView modelAndView = new ModelAndView("new-customer");
        Customer customer = new Customer();
        modelAndView.addObject("customer",customer);
        logger.info("Admin trying to add  a new customer");
        return modelAndView;
    }

    @GetMapping("/admin/add-admin")
    public ModelAndView addAdmin() {
        ModelAndView modelAndView = new ModelAndView("new-admin");
        Customer customer = new Customer(); // Create a new instance of Customer
        modelAndView.addObject("customer", customer); // Add the new instance to the model
        logger.info("Admin trying to add a new admin");
        return modelAndView;
    }

    @GetMapping("/admin/bookings")
    public ModelAndView getAllBookings() {
        ModelAndView modelAndView = new ModelAndView("admin-bookings");
        List<Booking> bookingList = bookingService.findAll();
        modelAndView.addObject("bookingList", bookingList);
        logger.info("Admin viewing all bookings");
        return modelAndView;
    }

    @PostMapping("/save-admin")
    public String saveAdmin(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {
        customer.setRole("ADMIN"); // Set role to ADMIN
        customerService.SaveCustomer(customer);
        redirectAttributes.addFlashAttribute("successMessage", "Admin successfully added!");
        return "redirect:/admin/vehicles";
    }

    @GetMapping("/admin/profile")
public ModelAndView getAdminProfile() {
    Integer adminId = getCurrentAdminId();
    if (adminId == null) {
        return new ModelAndView("error-page").addObject("errorMessage", "Admin not found or not authenticated");
    }

    ModelAndView modelAndView = new ModelAndView("admin-profile");
    Customer admin = customerService.FindCustomerById(adminId);
    modelAndView.addObject("customer", admin);
    return modelAndView;
}


    @GetMapping("/admin/chat")
    public ModelAndView chat() {
        return new ModelAndView("chat");
    }

    @GetMapping("/admin/messages")
    public ModelAndView adminMessages(@RequestParam(required = false) Integer userId) {
        List<Customer> customers = customerService.getCustomersWithMessages();
        List<Message> messages = userId != null ? messageService.getMessagesByUserId(userId) : List.of();
        ModelAndView modelAndView = new ModelAndView("admin-messages");
        modelAndView.addObject("customers", customers);
        modelAndView.addObject("messages", messages);
        modelAndView.addObject("selectedUserId", userId);
        return modelAndView;
    }

    @PostMapping("/reply-message")
    public String replyMessage(@RequestParam int userId, @RequestParam String content) {
        Customer admin = customerService.FindCustomerById(getCurrentAdminId());

        Message message = new Message();
        message.setUserId(userId);
        message.setUserName(admin.getCustomerFirstName() + " " + admin.getCustomerLastName());
        message.setContent(content);
        message.setSenderType("ADMIN");

        messageService.saveMessage(message);
        return "redirect:/admin/messages";
    }

    @PostMapping("/update-admin")
    public String updateAdmin(@ModelAttribute Customer customer) {
        customer.setRole("ADMIN");
        customerService.SaveCustomer(customer);
        return "redirect:/admin/vehicles"; // Redirect to admin-cars page
    }


    @PostMapping("/save-cars")
public String saveCar(
        @ModelAttribute Car car,
        BindingResult result,
        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
        RedirectAttributes redirectAttributes
) {
    try {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation failed");
            return "redirect:/admin/add-vehicle";
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String uploadDir = "uploads/";
            String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String fileName = UUID.randomUUID().toString() + "_" + originalFilename;

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            car.setCarImage("/uploads/" + fileName);
        }

        car.setCarStatus("Available"); // Set status to Available by default
        carService.SaveCar(car);
        redirectAttributes.addFlashAttribute("successMessage", "Vehicle successfully added!");
        return "redirect:/admin/vehicles";

    } catch (IOException e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Error occurred while processing the image");
        return "redirect:/admin/add-vehicle";
    }
}


    private Integer getCurrentAdminId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
        logger.error("Authentication is null or not authenticated");
        return null;
    }

    String username = authentication.getName();
    if (username == null) {
        logger.error("Username is null");
        return null;
    }

    Customer admin = customerService.findByCustomerUserName(username);
    if (admin == null) {
        logger.error("Admin not found for username: " + username);
        return null;
    }

    logger.info("Admin found: " + admin.getCustomerId());
    return admin.getCustomerId();
}

    // src/main/java/com/cars/cars/Controller/AdminController.java
    @GetMapping("/admin/modification-requests")
    public ModelAndView getModificationRequests() {
        ModelAndView modelAndView = new ModelAndView("modification-requests");
        List<Booking> modificationRequests = bookingService.findAllByStatus("Modification Pending");

        // Set carName for each booking
        for (Booking booking : modificationRequests) {
            Car car = carService.findCarById(booking.getCarId());
            booking.setCarName(car.getCarName());
        }

        modelAndView.addObject("modificationRequests", modificationRequests);
        logger.info("Admin viewing modification requests");
        return modelAndView;
    }

    @PostMapping("/confirm-modification")
    public String confirmModification(@RequestParam Integer bookingId) {
        Booking booking = bookingService.FindBooking(bookingId);
        booking.setStatus("Reserved");
        bookingService.BookingSave(booking);

        Car car = carService.findCarById(booking.getCarId());
        car.setCarStatus("Reserved");
        carService.SaveCar(car);

        logger.info("Admin confirmed modification with ID: " + bookingId + " and car status set to Reserved");
        return "redirect:/admin/modification-requests";
    }

    @PostMapping("/reject-modification")
    public String rejectModification(@RequestParam Integer bookingId) {
        Booking booking = bookingService.FindBooking(bookingId);
        booking.setStatus("Reserved");
        bookingService.BookingSave(booking);

        logger.info("Admin rejected modification with ID: " + bookingId);
        return "redirect:/admin/modification-requests";
    }

}
