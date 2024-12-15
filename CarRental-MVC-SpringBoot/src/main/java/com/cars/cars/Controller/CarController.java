package com.cars.cars.Controller;

import com.cars.cars.Model.Booking;
import com.cars.cars.Model.Car;
import com.cars.cars.Service.BookingService;
import com.cars.cars.Service.CarServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Controller
public class CarController {

    @Autowired
    private CarServices carServices;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/search")
    public String searchCars(
            @RequestParam(value = "start-date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "end-date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "vehicle-type", required = false) String type,
            @RequestParam(value = "min-price", required = false) Integer minPrice,
            @RequestParam(value = "max-price", required = false) Integer maxPrice,
            Model model) {

        Date startDateConverted = startDate != null ? Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
        Date endDateConverted = endDate != null ? Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;

        List<Car> carList = carServices.searchCars(startDateConverted, endDateConverted, type, minPrice, maxPrice);
        model.addAttribute("carList", carList);
        return "cars";
    }

    @GetMapping("/car-details")
    public ModelAndView getCarDetails(@RequestParam Integer carId) {
        ModelAndView modelAndView = new ModelAndView("car-details");
        Car car = carServices.findCarById(carId);
        List<Booking> futureBookings = bookingService.findFutureBookingsByCarId(carId);
        modelAndView.addObject("car", car);
        modelAndView.addObject("futureBookings", futureBookings);
        return modelAndView;
    }

    @GetMapping("/cars/all")
    public ModelAndView showAllCars() {
        ModelAndView modelAndView = new ModelAndView("cars");
        List<Car> carList = carServices.GetAllCars();
        modelAndView.addObject("carList", carList);
        return modelAndView;
    }

    @GetMapping("/cars/non-reserved")
    public ModelAndView showNonReservedCars() {
        ModelAndView modelAndView = new ModelAndView("cars");
        List<Car> carList = carServices.findNonReservedCars();
        modelAndView.addObject("carList", carList);
        return modelAndView;
    }

    @GetMapping("/cars")
    public String showCars(Model model) {
        List<Car> carList = carServices.GetAllCars();
        List<Booking> bookingList = bookingService.findAll();
        model.addAttribute("carList", carList);
        model.addAttribute("bookingList", bookingList);
        return "cars";
    }

    @GetMapping("/admin/payments")
    public String showPayedBookings(Model model) {
        List<Booking> payedBookings = bookingService.findPayedBookings();
        System.out.println(payedBookings);
        model.addAttribute("payedBookings", payedBookings);
        return "admin-payments";
    }
}