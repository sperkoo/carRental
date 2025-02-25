package com.cars.cars.Service;

import com.cars.cars.Model.Booking;
import com.cars.cars.Repository.BookingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService implements BookingServices {

    @Autowired
    private BookingRepo bookingRepo;

    @Override
    public List<Booking> FindAllByCustomerId(int customerId) {
        return bookingRepo.findAllByCustomerId(customerId);
    }

    @Override
    public void BookingSave(Booking booking) {
        bookingRepo.save(booking);
    }

    @Override
    public Booking FindBooking(int bookingId) {
        Optional<Booking> optional = bookingRepo.findById(bookingId);
        Booking booking;
        if (optional.isPresent()) {
            booking = optional.get();
        } else {
            throw new RuntimeException("Car not found for id : " + bookingId);
        }
        return booking;
    }

    @Override
    public void DeleteBooking(int bookingId) {
        bookingRepo.deleteById(bookingId);
    }

    @Override
    public List<Booking> findAll() {
        return bookingRepo.findAll();
    }

    @Override
    public List<Booking> findFutureBookingsByCarId(int carId) {
        return bookingRepo.findFutureBookingsByCarId(carId);
    }

    @Override
    public List<Booking> findAllByStatus(String status) {
        return bookingRepo.findAllByStatus(status);
    }

    @Override
    public List<Booking> findAllByDate(LocalDate date) {
        return bookingRepo.findAllByDate(date.toString());
    }

    @Override
    public List<Booking> findAllByDateRange(LocalDate startDate, LocalDate endDate) {
        return bookingRepo.findAllByDateRange(startDate.toString(), endDate.toString());
    }

    public boolean isBookingConflict(int carId, String startDate, String endDate) {
        List<Booking> futureBookings = bookingRepo.findFutureBookingsByCarId(carId);
        for (Booking booking : futureBookings) {
            if ((startDate.compareTo(booking.getBookingDateTo()) <= 0) && (endDate.compareTo(booking.getBookingDateFrom()) >= 0)) {
                return true;
            }
        }
        return false;
    }

    public List<Booking> findPayedBookings() {
        List<Object[]> results = bookingRepo.findPayedBookingsWithCarName();
        return results.stream().map(result -> {
            Booking booking = (Booking) result[0];
            booking.setCarName((String) result[1]);
            return booking;
        }).collect(Collectors.toList());
    }
}