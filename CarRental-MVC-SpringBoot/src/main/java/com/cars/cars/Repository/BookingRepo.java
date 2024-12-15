package com.cars.cars.Repository;

import com.cars.cars.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByCustomerId(int customerId);
    List<Booking> findAllByStatus(String status);

    @Query("SELECT b FROM Booking b WHERE b.carId = :carId AND b.bookingDateFrom >= CURRENT_DATE")
    List<Booking> findFutureBookingsByCarId(@Param("carId") Integer carId);

    @Query("SELECT b FROM Booking b JOIN Car c ON b.carId = c.carId WHERE b.status = 'Payed'")
    List<Booking> findPayedBookings();

    @Query("SELECT b, c.carName FROM Booking b JOIN Car c ON b.carId = c.carId WHERE b.status = 'Payed'")
    List<Object[]> findPayedBookingsWithCarName();

    @Query("SELECT b FROM Booking b WHERE b.bookingDateFrom = :date")
    List<Booking> findAllByDate(@Param("date") LocalDate date);

    @Query("SELECT b FROM Booking b WHERE b.bookingDateFrom BETWEEN :startDate AND :endDate")
    List<Booking> findAllByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT b FROM Booking b WHERE b.status = :status AND b.bookingDateFrom BETWEEN :startDate AND :endDate")
    List<Booking> findAllByStatusAndDateRange(@Param("status") String status, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}