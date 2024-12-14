// CarService.java
package com.cars.cars.Service;

import com.cars.cars.Model.Car;
import com.cars.cars.Repository.CarRepo;
import com.cars.cars.Specification.CarSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CarService implements CarServices {

    @Autowired
    private CarRepo carRepo;

    @Override
    public List<Car> searchCars(Date startDate, Date endDate, String type, Integer minPrice, Integer maxPrice) {
        Specification<Car> spec = Specification.where(null);

        if (startDate != null && endDate != null) {
            spec = spec.and(CarSpecification.isAvailableBetween(startDate, endDate));
        }
        if (type != null && !type.isEmpty()) {
            spec = spec.and(CarSpecification.hasType(type));
        }
        if (minPrice != null || maxPrice != null) {
            spec = spec.and(CarSpecification.hasPriceRange(minPrice, maxPrice));
        }

        return carRepo.findAll(spec);
    }

    @Override
    public List<Car> findAllByCarStatusTrue() {
        return carRepo.findAllByCarStatus("Available");
    }

    @Override
    public List<Car> GetAllCars() {
        return carRepo.findAll();
    }

    @Override
    public Car findCarById(int carId) {
        Optional<Car> optional = carRepo.findById(carId);
        Car car;
        if (optional.isPresent()) {
            car = optional.get();
        } else {
            throw new RuntimeException("Car not found for id : " + carId);
        }
        return car;
    }

    @Override
    public List<Car> findNonReservedCars() {
        return carRepo.findByCarStatus("Available"); // Fetch only non-reserved cars
    }

    @Override
    public void SaveCar(Car car) {
        carRepo.save(car);
    }

    @Override
    public void DeleteCar(int carId) {
        carRepo.deleteById(carId);
    }

    public void updateCarStatus(Integer carId, String status) {
        Car car = carRepo.findById(carId).orElseThrow(() -> new IllegalArgumentException("Invalid car Id:" + carId));
        car.setCarStatus(status);
        carRepo.save(car);
    }

    @Override
    public List<Car> findAllByCarStatus(String status) {
        return carRepo.findAllByCarStatus(status);
    }
}