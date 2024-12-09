package com.cars.cars.Controller;

import com.cars.cars.Model.Booking;
import com.cars.cars.Model.Car;
import com.cars.cars.Model.Customer;
import com.cars.cars.Service.BookingService;
import com.cars.cars.Service.CarService;
import com.cars.cars.Service.CustomerService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Controller
public class PdfController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private CarService carService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/generate-contract")
    public ResponseEntity<InputStreamResource> generateContract(@RequestParam Integer bookingId) throws DocumentException, IOException {
        Booking booking = bookingService.FindBooking(bookingId);
        Car car = carService.findCarById(booking.getCarId());
        Customer customer = customerService.FindCustomerById(booking.getCustomerId());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();
        document.add(new Paragraph("Booking Contract"));
        document.add(new Paragraph("Customer: " + customer.getCustomerFirstName() + " " + customer.getCustomerLastName()));
        document.add(new Paragraph("Car: " + car.getCarName() + " " + car.getCarModel()));
        document.add(new Paragraph("Booking Date From: " + booking.getBookingDateFrom()));
        document.add(new Paragraph("Booking Date To: " + booking.getBookingDateTo()));
        document.add(new Paragraph("Total Price: " + booking.getTotalPrice()));
        document.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "contract.pdf");

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(bis));
    }

    @GetMapping("/generate-invoice")
public ResponseEntity<InputStreamResource> generateInvoice(@RequestParam Integer bookingId) throws DocumentException, IOException {
    Booking booking = bookingService.FindBooking(bookingId);
    Car car = carService.findCarById(booking.getCarId());
    Customer customer = customerService.FindCustomerById(booking.getCustomerId());

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Document document = new Document();
    PdfWriter.getInstance(document, out);
    document.open();
    document.add(new Paragraph("Invoice"));
    document.add(new Paragraph("Customer: " + customer.getCustomerFirstName() + " " + customer.getCustomerLastName()));
    document.add(new Paragraph("Car: " + car.getCarName() + " " + car.getCarModel()));
    document.add(new Paragraph("Booking Date From: " + booking.getBookingDateFrom()));
    document.add(new Paragraph("Booking Date To: " + booking.getBookingDateTo()));
    document.add(new Paragraph("Total Price: " + booking.getTotalPrice()));
    document.close();

    ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("attachment", "invoice.pdf");

    return ResponseEntity.ok().headers(headers).body(new InputStreamResource(bis));
}
}