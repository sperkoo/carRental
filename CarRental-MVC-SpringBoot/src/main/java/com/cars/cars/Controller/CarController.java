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
            @RequestParam(value = "start-date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "end-date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(value = "vehicle-type", required = false) String type,
            @RequestParam(value = "min-price", required = false) Integer minPrice,
            @RequestParam(value = "max-price", required = false) Integer maxPrice,
            Model model) {

        List<Car> carList = carServices.searchCars(startDate, endDate, type, minPrice, maxPrice);
        model.addAttribute("carList", carList);
        return "cars";
    }

@GetMapping("/car-details")
public ModelAndView getCarDetails(@RequestParam Integer carId) {
    ModelAndView modelAndView = new ModelAndView("car-details");
    Car car = carServices.findCarById(carId);
    List<Booking> futureBookings = bookingService.findFutureBookingsByCarId(carId); // Ajoutez cette ligne
    modelAndView.addObject("car", car);
    modelAndView.addObject("futureBookings", futureBookings); // Ajoutez cette ligne
    return modelAndView;
}

    @GetMapping("/cars/all")
    public ModelAndView showAllCars() {
        ModelAndView modelAndView = new ModelAndView("cars");
        List<Car> carList = carServices.GetAllCars(); // Fetch all cars, including reserved ones
        modelAndView.addObject("carList", carList);
        return modelAndView;
    }

    @GetMapping("/cars/non-reserved")
    public ModelAndView showNonReservedCars() {
        ModelAndView modelAndView = new ModelAndView("cars");
        List<Car> carList = carServices.findNonReservedCars(); // Fetch only non-reserved cars
        modelAndView.addObject("carList", carList);
        return modelAndView;
    }

    @GetMapping("/cars")
    public String showCars(Model model) {
        List<Car> carList = carServices.GetAllCars();
        List<Booking> bookingList = bookingService.findAll(); // Fetch all bookings
        model.addAttribute("carList", carList);
        model.addAttribute("bookingList", bookingList); // Add bookings to the model
        return "cars";
    }

    @GetMapping("/admin/payments")
    public String showPayedCars(Model model) {
        List<Car> payedCarsList = carServices.findAllByCarStatus("Payed");
        model.addAttribute("payedCarsList", payedCarsList);
        return "admin-payments";
    }
}