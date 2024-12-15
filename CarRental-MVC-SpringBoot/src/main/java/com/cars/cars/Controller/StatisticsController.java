package com.cars.cars.Controller;

import com.cars.cars.Service.CarRentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.logging.Logger;

@Controller
public class StatisticsController {

    @Autowired
    private CarRentalService carRentalService;

    private static final Logger logger = Logger.getLogger(StatisticsController.class.getName());

    @GetMapping("/admin/statistics")
    public String getStatistics(Model model) {
        int totalReservedCars = carRentalService.getTotalReservedCars();
        int totalAvailableCars = carRentalService.getTotalAvailableCars();
        int totalMaintenanceCars = carRentalService.getTotalMaintenanceCars();
        int totalAmount = carRentalService.getTotalAmount();
        int totalReservedCarsToday = carRentalService.getTotalReservedCarsToday();
        int totalAmountToday = carRentalService.getTotalAmountToday();
        int totalReservedCarsWeek = carRentalService.getTotalReservedCarsWeek();
        int totalAmountWeek = carRentalService.getTotalAmountWeek();

        int totalPayedCars = carRentalService.getTotalPayedCars();
        int totalPayedCarsToday = carRentalService.getTotalPayedCarsToday();
        int totalPayedCarsWeek = carRentalService.getTotalPayedCarsWeek();

        logger.info("Total Reserved Cars: " + totalReservedCars);
        logger.info("Total Available Cars: " + totalAvailableCars);
        logger.info("Total Maintenance Cars: " + totalMaintenanceCars);
        logger.info("Total Amount: " + totalAmount);
        logger.info("Total Reserved Cars Today: " + totalReservedCarsToday);
        logger.info("Total Amount Today: " + totalAmountToday);
        logger.info("Total Reserved Cars This Week: " + totalReservedCarsWeek);
        logger.info("Total Amount This Week: " + totalAmountWeek);

        model.addAttribute("totalReservedCars", totalReservedCars);
        model.addAttribute("totalAvailableCars", totalAvailableCars);
        model.addAttribute("totalMaintenanceCars", totalMaintenanceCars);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("totalReservedCarsToday", totalReservedCarsToday);
        model.addAttribute("totalAmountToday", totalAmountToday);
        model.addAttribute("totalReservedCarsWeek", totalReservedCarsWeek);
        model.addAttribute("totalAmountWeek", totalAmountWeek);

        model.addAttribute("totalPayedCars", totalPayedCars);
        model.addAttribute("totalPayedCarsToday", totalPayedCarsToday);
        model.addAttribute("totalPayedCarsWeek", totalPayedCarsWeek);

        // Add data for the chart
        List<Integer> totalAmounts = carRentalService.getTotalAmountsOverTime();
        model.addAttribute("totalAmounts", totalAmounts);

        return "statistics";
    }
}