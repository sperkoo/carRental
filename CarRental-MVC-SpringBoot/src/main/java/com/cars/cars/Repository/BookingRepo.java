package com.cars.cars.Repository;

import com.cars.cars.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByCustomerId(int customerId);
    List<Booking> findAllByStatus(String status);

    @Query("SELECT b FROM Booking b WHERE b.carId = :carId AND b.bookingDateFrom >= CURRENT_DATE")
    List<Booking> findFutureBookingsByCarId(@Param("carId") Integer carId);
}