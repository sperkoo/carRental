package com.cars.cars.Service;

import com.cars.cars.Model.Booking;

import java.util.List;

public interface BookingServices {
    List<Booking> FindAllByCustomerId(int customerId);
    void BookingSave(Booking booking);
    Booking FindBooking(int bookingId);
    void DeleteBooking(int bookingId);
    List<Booking> findAll();
    List<Booking> findFutureBookingsByCarId(int carId); // Add this line

    List<Booking> findAllByStatus(String status);
}
