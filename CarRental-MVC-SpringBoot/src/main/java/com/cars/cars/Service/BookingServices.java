package com.cars.cars.Service;

import com.cars.cars.Model.Booking;

import java.time.LocalDate;
import java.util.List;

public interface BookingServices {
    List<Booking> FindAllByCustomerId(int customerId);
    void BookingSave(Booking booking);
    Booking FindBooking(int bookingId);
    void DeleteBooking(int bookingId);
    List<Booking> findAll();
    List<Booking> findFutureBookingsByCarId(int carId); // Add this line

    List<Booking> findAllByStatus(String status);
    List<Booking> findAllByDate(LocalDate date);
    List<Booking> findAllByDateRange(LocalDate startDate, LocalDate endDate);
}
