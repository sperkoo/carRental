package com.cars.cars.Service;

import com.cars.cars.Model.Booking;
import com.cars.cars.Model.Car;
import com.cars.cars.Repository.BookingRepo;
import com.cars.cars.Repository.CarRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class CarRentalService {

    @Autowired
    private CarRepo carRepo;

    @Autowired
    private BookingRepo bookingRepo;

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

    public int getTotalReservedCarsToday() {
        LocalDate today = LocalDate.now();
        return (int) bookingRepo.findAllByStatus("Reserved").stream()
                .filter(booking -> LocalDate.parse(booking.getBookingDateFrom()).equals(today))
                .count();
    }

    public int getTotalAmountToday() {
        LocalDate today = LocalDate.now();
        return (int) bookingRepo.findAllByStatus("Payed").stream()
                .filter(booking -> LocalDate.parse(booking.getBookingDateFrom()).equals(today))
                .mapToDouble(Booking::getTotalPrice)
                .sum();
    }

    public int getTotalReservedCarsWeek() {
        LocalDate now = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeek = now.get(weekFields.weekOfWeekBasedYear());
        return (int) bookingRepo.findAllByStatus("Reserved").stream()
                .filter(booking -> LocalDate.parse(booking.getBookingDateFrom()).get(weekFields.weekOfWeekBasedYear()) == currentWeek)
                .count();
    }

    public List<Integer> getTotalAmountsOverTime() {
        // Implement logic to get total amounts over time
        // This is a placeholder. Replace with actual logic to get total amounts over time.
        return List.of(1000, 1500, 2000, 2500, 3000);
    }

    public int getTotalAmountWeek() {
        LocalDate now = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeek = now.get(weekFields.weekOfWeekBasedYear());
        return (int) bookingRepo.findAllByStatus("Payed").stream()
                .filter(booking -> LocalDate.parse(booking.getBookingDateFrom()).get(weekFields.weekOfWeekBasedYear()) == currentWeek)
                .mapToDouble(Booking::getTotalPrice)
                .sum();
    }
}
