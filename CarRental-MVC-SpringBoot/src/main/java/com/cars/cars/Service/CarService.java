// CarService.java
package com.cars.cars.Service;

import com.cars.cars.Model.Car;
import com.cars.cars.Repository.CarRepo;
import com.cars.cars.Specification.CarSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class CarService implements CarServices {

    @Autowired
    private CarRepo carRepo;

    @Autowired
    private BookingService bookingService;

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
public List<Car> searchAvailableCars(LocalDate startDate, LocalDate endDate, String type, Integer minPrice, Integer maxPrice) {
    Specification<Car> spec = Specification.where(null);

    if (type != null && !type.isEmpty()) {
        spec = spec.and(CarSpecification.hasType(type));
    }

    if (minPrice != null || maxPrice != null) {
        spec = spec.and(CarSpecification.hasPriceRange(minPrice, maxPrice));
    }

    List<Car> availableCars = carRepo.findAll(Specification.where(spec).and(CarSpecification.hasStatus("Available")));
    List<Car> reservedCars = carRepo.findAll(Specification.where(spec).and(CarSpecification.hasStatus("Reserved")));

    if (startDate != null && endDate != null) {
        reservedCars = reservedCars.stream()
                .filter(car -> !bookingService.isBookingConflict(car.getCarId(), startDate.toString(), endDate.toString()))
                .collect(Collectors.toList());
    }

    availableCars.addAll(reservedCars);
    return availableCars;
}


//    @Override
//    public List<Car> searchAvailableCars(LocalDate startDate, LocalDate endDate, String type, Integer minPrice, Integer maxPrice) {
//        List<Car> availableCars = carRepo.findAllByCarStatus("Available");
//        List<Car> reservedCars = carRepo.findAllByCarStatus("Reserved");
//
//        final LocalDate finalStartDate = startDate;
//        final LocalDate finalEndDate = endDate;
//
//        if (finalStartDate != null && finalEndDate != null) {
//            reservedCars = reservedCars.stream()
//                    .filter(car -> !bookingService.isBookingConflict(car.getCarId(), finalStartDate.toString(), finalEndDate.toString()))
//                    .collect(Collectors.toList());
//        }
//
//        Predicate<Car> typePredicate = car -> type == null || type.isEmpty() || car.getCarType().equalsIgnoreCase(type);
//        Predicate<Car> pricePredicate = car -> (minPrice == null || car.getCarPrice() >= minPrice) && (maxPrice == null || car.getCarPrice() <= maxPrice);
//
//        List<Car> filteredAvailableCars = availableCars.stream()
//                .filter(typePredicate.and(pricePredicate))
//                .collect(Collectors.toList());
//
//        List<Car> filteredReservedCars = reservedCars.stream()
//                .filter(typePredicate.and(pricePredicate))
//                .collect(Collectors.toList());
//
//        filteredAvailableCars.addAll(filteredReservedCars);
//        return filteredAvailableCars;
//    }



//    @Override
//    public List<Car> searchAvailableCars(LocalDate startDate, LocalDate endDate, String type, Integer minPrice, Integer maxPrice) {
//        Specification<Car> spec = Specification.where(null);
//        List<Car> availableCars = carRepo.findAllByCarStatus("Available");
//        List<Car> reservedCars = carRepo.findAllByCarStatus("Reserved");
//
//        final LocalDate finalStartDate = startDate;
//        final LocalDate finalEndDate = endDate;
//
//        if (finalStartDate != null && finalEndDate != null) {
//            reservedCars = reservedCars.stream()
//                    .filter(car -> !bookingService.isBookingConflict(car.getCarId(), finalStartDate.toString(), finalEndDate.toString()))
//                    .collect(Collectors.toList());
//        }
//
//        if (type != null && !type.isEmpty()) {
//            spec = spec.and(CarSpecification.hasType(type));
//        }
//
//        if (minPrice != null || maxPrice != null) {
//            spec = spec.and(CarSpecification.hasPriceRange(minPrice, maxPrice));
//        }
//
//        List<Car> filteredAvailableCars = availableCars.stream()
//                .filter(car -> spec.toPredicate(car, null, null))
//                .collect(Collectors.toList());
//
//        List<Car> filteredReservedCars = reservedCars.stream()
//                .filter(car -> spec.toPredicate(car, null, null))
//                .collect(Collectors.toList());
//
//        filteredAvailableCars.addAll(filteredReservedCars);
//        return filteredAvailableCars;
//    }

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