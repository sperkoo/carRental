package com.cars.cars.Service;

import com.cars.cars.Model.Car;

import java.util.Date;
import java.util.List;

public interface CarServices {
    List<Car> findAllByCarStatusTrue();
    List<Car> GetAllCars();
    Car findCarById(int carId);
    void SaveCar(Car car);
    void DeleteCar(int carId);
    List<Car> searchCars(Date startDate, Date endDate, String type, Integer minPrice, Integer maxPrice);
    List<Car> findNonReservedCars();
    List<Car> findAllByCarStatus(String status); // Add this method
}