// CarRentalService.java
package com.cars.cars.Service;

import com.cars.cars.Model.Booking;
import com.cars.cars.Repository.BookingRepo;
import com.cars.cars.Repository.CarRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@Service
public class CarRentalService {

    @Autowired
    private CarRepo carRepo;

    @Autowired
    private BookingRepo bookingRepo;

    private static final Logger logger = Logger.getLogger(CarRentalService.class.getName());

    public int getTotalReservedCars() {
        return carRepo.findAllByCarStatus("Reserved").size();
    }

    public int getTotalAmount() {
        return (int) bookingRepo.findAllByStatus("Payed").stream()
                .mapToDouble(Booking::getTotalPrice)
                .sum();
    }

    public int getTotalAvailableCars() {
        return carRepo.countByCarStatus("Available");
    }

    public int getTotalMaintenanceCars() {
        return carRepo.countByCarStatus("Maintenance");
    }

    public int getTotalReservedCarsToday() {
        Logger logger = Logger.getLogger(CarRentalService.class.getName());
        LocalDate today = LocalDate.now();
        logger.info("Calculating total reserved cars for today: " + today);

        long totalReservedCarsToday = bookingRepo.findAllByStatus("Reserved").stream()
                .filter(booking -> {
                    LocalDateTime createdDate = booking.getCreatedDate();
                    boolean isToday = createdDate != null && createdDate.toLocalDate().equals(today);
                    logger.info("Booking created date: " + createdDate + ", is today: " + isToday);
                    return isToday;
                })
                .count();

        logger.info("Total reserved cars today: " + totalReservedCarsToday);
        return (int) totalReservedCarsToday;
    }

    public int getTotalAmountToday() {
        LocalDate today = LocalDate.now();
        logger.info("Calculating total amount for today: " + today);

        double totalAmountToday = bookingRepo.findAllByStatus("Payed").stream()
                .filter(booking -> {
                    LocalDateTime createdDate = booking.getCreatedDate();
                    if (createdDate == null) {
                        logger.warning("Booking with ID " + booking.getBookingId() + " has null createdDate");
                        return false;
                    }
                    LocalDate bookingCreatedDate = createdDate.toLocalDate();
                    boolean isToday = bookingCreatedDate.equals(today);
                    logger.info("Booking created date: " + bookingCreatedDate + ", is today: " + isToday);
                    return isToday;
                })
                .mapToDouble(Booking::getTotalPrice)
                .sum();

        logger.info("Total amount today: " + totalAmountToday);
        return (int) totalAmountToday;
    }

    public int getTotalReservedCarsWeek() {
        Logger logger = Logger.getLogger(CarRentalService.class.getName());
        LocalDate now = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeek = now.get(weekFields.weekOfWeekBasedYear());
        logger.info("Calculating total reserved cars for the current week: " + currentWeek);

        long totalReservedCarsWeek = bookingRepo.findAllByStatus("Reserved").stream()
                .filter(booking -> {
                    LocalDateTime createdDate = booking.getCreatedDate();
                    if (createdDate == null) {
                        return false;
                    }
                    int bookingWeek = createdDate.toLocalDate().get(weekFields.weekOfWeekBasedYear());
                    return bookingWeek == currentWeek;
                })
                .count();

        logger.info("Total reserved cars this week: " + totalReservedCarsWeek);
        return (int) totalReservedCarsWeek;
    }

    public List<Integer> getTotalAmountsOverTime() {
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