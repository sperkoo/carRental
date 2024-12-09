package com.cars.cars.Controller;

import com.cars.cars.Service.CarRentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class StatisticsController {

    @Autowired
    private CarRentalService carRentalService;

    @GetMapping("/admin/statistics")
    public String getStatistics(Model model) {
        int totalReservedCars = carRentalService.getTotalReservedCars();
        int totalAvailableCars = carRentalService.getTotalAvailableCars();
        int totalMaintenanceCars = carRentalService.getTotalMaintenanceCars();
        int totalAmount = carRentalService.getTotalAmount();

        model.addAttribute("totalReservedCars", totalReservedCars);
        model.addAttribute("totalAvailableCars", totalAvailableCars);
        model.addAttribute("totalMaintenanceCars", totalMaintenanceCars);
        model.addAttribute("totalAmount", totalAmount);

        // Add data for the chart
        List<Integer> totalAmounts = carRentalService.getTotalAmountsOverTime();
        model.addAttribute("totalAmounts", totalAmounts);

        return "statistics";
    }
}