package com.cars.cars.Controller;

import com.cars.cars.Model.Booking;
import com.cars.cars.Model.Car;
import com.cars.cars.Service.BookingService;
import com.cars.cars.Service.CarService;
import com.cars.cars.Service.PayPalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class PayPalController {

    @Autowired
    private PayPalService payPalService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private CarService carService;

    @PostMapping("/pay")
    public RedirectView pay(@RequestParam("bookingId") Long bookingId, @RequestParam("totalPrice") Double totalPrice) {
        try {
            Payment payment = payPalService.createPayment(
                    totalPrice / 10, // Use the totalPrice from the request
                    "USD",
                    "paypal",
                    "sale",
                    "Payment description",
                    "http://localhost:8080/cancel",
                    "http://localhost:8080/success?bookingId=" + bookingId
            );
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return new RedirectView(link.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return new RedirectView("/error");
    }

    @GetMapping("/cancel")
    public String cancelPay() {
        return "cancel";
    }

    @GetMapping("/success")
    public String successPay(@RequestParam("bookingId") Long bookingId) {
        Booking booking = bookingService.FindBooking(Math.toIntExact(bookingId));
        Car car = carService.findCarById(booking.getCarId());
        car.setCarStatus("Payed");
        booking.setStatus("Payed");
        carService.SaveCar(car);
        return "success";
    }
}