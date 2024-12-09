package com.cars.cars.Controller;

import com.cars.cars.Model.Booking;
import com.cars.cars.Model.Car;
import com.cars.cars.Model.Customer;
import com.cars.cars.Service.BookingService;
import com.cars.cars.Service.CarService;
import com.cars.cars.Service.CustomerService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
    car.setCarStatus("Reserved"); // Set status to Reserved
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


}
