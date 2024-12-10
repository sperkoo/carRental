package com.cars.cars.Repository;

import com.cars.cars.Model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CarRepo extends JpaRepository<Car, Integer>, JpaSpecificationExecutor<Car> {
    List<Car> findAllByCarStatus(String status);
    List<Car> findByCarStatusAndRentalDate(String carStatus, LocalDate rentalDate);
    int countByCarStatus(String status); // Add this method
    List<Car> findByCarStatus(String status);

}