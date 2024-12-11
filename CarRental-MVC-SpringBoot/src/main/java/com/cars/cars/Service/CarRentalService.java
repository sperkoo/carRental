package com.cars.cars.Service;

import com.cars.cars.Model.Car;
import com.cars.cars.Repository.CarRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarRentalService {

    @Autowired
    private CarRepo carRepo;

    public int getTotalReservedCars() {
        return carRepo.findAllByCarStatus("Reserved").size();
    }

    public int getTotalAmount() {
        List<Car> reservedCars = carRepo.findAllByCarStatus("Payed");
        return reservedCars.stream().mapToInt(Car::getCarPrice).sum();
    }

    public int getTotalAvailableCars() {
        return carRepo.countByCarStatus("Available");
    }

    public int getTotalMaintenanceCars() {
        return carRepo.countByCarStatus("Maintenance");
    }

    public List<Integer> getTotalAmountsOverTime() {
        // This is a placeholder. Replace with actual logic to get total amounts over time.
        return List.of(1000, 1500, 2000, 2500, 3000);
    }
}