// src/main/java/com/cars/cars/Controller/UserController.java
package com.cars.cars.Controller;

import com.cars.cars.Model.Booking;
import com.cars.cars.Model.Car;
import com.cars.cars.Model.Customer;
import com.cars.cars.Model.Message;
import com.cars.cars.Service.BookingService;
import com.cars.cars.Service.CarService;
import com.cars.cars.Service.CarServices;
import com.cars.cars.Service.CustomerService;
import com.cars.cars.Service.MessageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    private CarService carService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CarServices carServices;

    @Autowired
    private MessageService messageService;

    private Integer getCurrentCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Customer customer = customerService.findByCustomerUserName(username);
        return customer.getCustomerId();
    }

    @GetMapping("/")
    public ModelAndView GetHomeView(){
        ModelAndView modelAndView = new ModelAndView("home");
        logger.info("Someone in");
        return modelAndView;
    }

    @PostMapping("/send-booking-request")
    public String sendBookingRequest(@ModelAttribute Booking booking, @RequestParam Integer carId, Model model) {
        booking.setCarId(carId);
        booking.setCustomerId(getCurrentCustomerId());

        boolean isConflit = bookingService.isBookingConflict(booking.getCarId(), booking.getBookingDateFrom(), booking.getBookingDateTo());
        if (isConflit) {
            model.addAttribute("errorMessage", "Impossible de réserver dans cette date car la voiture est déjà réservée.");
            model.addAttribute("booking", booking);
            model.addAttribute("futureBookings", bookingService.findFutureBookingsByCarId(carId));
            return "bookingform";
        }

        Car car = carService.findCarById(carId);
        booking.setPriceDay(car.getCarPrice());
        booking.setImage(car.getCarImage());
        car.setCarStatus("Maintenance"); // Set status to Maintenance
        booking.setStatus("Pending");

        LocalDate dateBefore = LocalDate.parse(booking.getBookingDateFrom());
        LocalDate dateAfter = LocalDate.parse(booking.getBookingDateTo());
        long noOfDaysBetween = ChronoUnit.DAYS.between(dateBefore, dateAfter);
        if (noOfDaysBetween < 1) {
            booking.setTotalPrice(car.getCarPrice());
        } else {
            booking.setTotalPrice(car.getCarPrice() * noOfDaysBetween);
        }

        carService.SaveCar(car);
        bookingService.BookingSave(booking);
        logger.info("User sent a booking request and car status set to Maintenance");
        return "redirect:/user-cars";
    }

    @GetMapping("/user-cars")
    public ModelAndView GetAllCars(){
        ModelAndView modelAndView = new ModelAndView("cars");
        List<Car> carList = carService.findAllByCarStatusTrue();
        modelAndView.addObject("carList",carList);
        logger.info("User viewing available cars");
        return modelAndView;
    }

    @GetMapping("/settings")
    public ModelAndView GetSettings() {
        Integer customerId = getCurrentCustomerId();
        ModelAndView modelAndView = new ModelAndView("settings");
        Customer customer = customerService.FindCustomerById(customerId);
        modelAndView.addObject("customer", customer);
        logger.info("User accessing settings");
        return modelAndView;
    }

    @PostMapping("/update-user")
    public String UpdateUser(@ModelAttribute Customer customer, Model model) {
        customerService.SaveCustomer(customer);
        logger.info("User updated information");
        return "redirect:/user-cars";
    }

    @GetMapping("/orders")
    public ModelAndView GetMyOrders(){
        Integer customerId = getCurrentCustomerId();
        ModelAndView modelAndView = new ModelAndView("orders");
        List<Booking> bookingList = bookingService.FindAllByCustomerId(customerId);
        modelAndView.addObject("bookingList",bookingList);
        logger.info("User viewing orders");
        return modelAndView;
    }

    @GetMapping("/bookingform")
    public ModelAndView BookingCar(@RequestParam Integer carId){
        ModelAndView modelAndView = new ModelAndView("bookingform");
        Car car = carService.findCarById(carId);
        Booking booking = new Booking();
        booking.setCarId(car.getCarId());
        booking.setCustomerId(getCurrentCustomerId());
        List<Booking> futureBookings = bookingService.findFutureBookingsByCarId(carId);
        modelAndView.addObject("booking", booking);
        modelAndView.addObject("futureBookings", futureBookings);
        logger.info("Some user trying to rent a car");
        return modelAndView;
    }

    @PostMapping("/save-booking")
    public String SaveBooking(@ModelAttribute Booking booking, @RequestParam Integer carId){
        Car car = carService.findCarById(carId);
        car.setCarStatus("Maintenance");
        LocalDate dateBefore = LocalDate.parse(booking.getBookingDateFrom());
        LocalDate dateAfter = LocalDate.parse(booking.getBookingDateTo());
        long noOfDaysBetween = ChronoUnit.DAYS.between(dateBefore, dateAfter);
        if(noOfDaysBetween <1){
            booking.setTotalPrice(car.getCarPrice() );
        }
        else{
            booking.setTotalPrice(car.getCarPrice() * noOfDaysBetween);
        }

        booking.setCustomerId(getCurrentCustomerId());
        booking.setCarId(carId);
        booking.setImage(car.getCarImage());
        booking.setPriceDay(car.getCarPrice());
        carService.SaveCar(car);
        bookingService.BookingSave(booking);
        logger.info("User rented a car");
        return "redirect:/user-cars";
    }

    @GetMapping("/update-booking")
    public ModelAndView UpdateBooking(@RequestParam Integer bookingId){
        ModelAndView modelAndView = new ModelAndView("update-booking");
        Booking booking = bookingService.FindBooking(bookingId);
        System.out.println(booking.getBookingId());
        modelAndView.addObject("booking",booking);
        logger.info("User trying to update booking");
        return modelAndView;
    }

    @PostMapping("/return-car")
    public String ReturnCar(@ModelAttribute Booking booking,@RequestParam Integer bookingId, @RequestParam Integer carId) {
        Booking booking1 = bookingService.FindBooking(bookingId);
        Car car = carService.findCarById(carId);
        if (booking1.getStatus().equals("Payed")) {
            car.setCarStatus("Under Inspection");
        }
        else {
            car.setCarStatus("Available");
        }
        bookingService.DeleteBooking(booking1.getBookingId());
        carService.SaveCar(car);
        logger.info("User returned a car");
        return "redirect:/user-cars";
    }

    // src/main/java/com/cars/cars/Controller/UserController.java
    @PostMapping("/save-update")
    public String SaveUpdate(@ModelAttribute Booking booking, @RequestParam Integer bookingId, Model model) {
        Booking existingBooking = bookingService.FindBooking(bookingId);
        boolean isConflict = isBookingConflict(existingBooking.getCarId(), booking.getBookingDateFrom(), booking.getBookingDateTo());

        if (isConflict) {
            model.addAttribute("errorMessage", "Impossible de réserver dans cette date car la voiture est déjà réservée.");
            model.addAttribute("booking", booking);
            return "update-booking";
        }

        LocalDate dateBefore = LocalDate.parse(booking.getBookingDateFrom());
        LocalDate dateAfter = LocalDate.parse(booking.getBookingDateTo());
        long noOfDaysBetween = ChronoUnit.DAYS.between(dateBefore, dateAfter);
        if (noOfDaysBetween < 1) {
            existingBooking.setTotalPrice(existingBooking.getPriceDay());
        } else {
            existingBooking.setTotalPrice(existingBooking.getPriceDay() * noOfDaysBetween);
        }
        existingBooking.setBookingDateFrom(booking.getBookingDateFrom());
        existingBooking.setBookingDateTo(booking.getBookingDateTo());
        existingBooking.setStatus("Modification Pending");
        bookingService.BookingSave(existingBooking);
        logger.info("User requested a booking modification");
        return "redirect:/user-cars";
    }

    @GetMapping("/chat")
    public ModelAndView chat() {
        Integer userId = getCurrentCustomerId();
        List<Message> messages = messageService.getMessagesByUserId(userId);
        ModelAndView modelAndView = new ModelAndView("chat");
        modelAndView.addObject("messages", messages);
        return modelAndView;
    }

    @PostMapping("/send-message")
    public String sendMessage(@RequestParam String content) {
        Integer userId = getCurrentCustomerId();
        Customer customer = customerService.FindCustomerById(userId);

        Message message = new Message();
        message.setUserId(userId);
        message.setUserName(customer.getCustomerFirstName() + " " + customer.getCustomerLastName());
        message.setContent(content);
        message.setSenderType("USER");

        messageService.saveMessage(message);
        return "redirect:/chat";
    }

    @GetMapping("/local")
    public ModelAndView viewLocal() {
        ModelAndView modelAndView = new ModelAndView("local");
        return modelAndView;
    }

    public boolean isBookingConflict(Integer carId, String startDate, String endDate) {
        return bookingService.isBookingConflict(carId, startDate, endDate);
    }



}