package com.cars.cars.Controller;

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

    @PostMapping("/pay")
    public RedirectView pay(@RequestParam("bookingId") Long bookingId, @RequestParam("totalPrice") Double totalPrice) {
        try {
            Payment payment = payPalService.createPayment(
                    totalPrice/10, // Use the totalPrice from the request
                    "USD",
                    "paypal",
                    "sale",
                    "Payment description",
                    "http://localhost:8080/cancel",
                    "http://localhost:8080/success"
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
    public String successPay() {
        return "success";
    }
}