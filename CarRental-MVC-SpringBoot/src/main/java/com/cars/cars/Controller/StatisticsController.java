package com.cars.cars.Controller;

import com.cars.cars.Model.Booking;
import com.cars.cars.Service.BookingService;
import com.cars.cars.Service.CarRentalService;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;


@Controller
public class StatisticsController {

    @Autowired
    private CarRentalService carRentalService;

    @Autowired
    private BookingService bookingService;

    private static final Logger logger = Logger.getLogger(StatisticsController.class.getName());

    @GetMapping("/admin/statistics")
    public String getStatistics(Model model) {
        int totalReservedCars = carRentalService.getTotalReservedCars();
        int totalAvailableCars = carRentalService.getTotalAvailableCars();
        int totalMaintenanceCars = carRentalService.getTotalMaintenanceCars();
        int totalAmount = carRentalService.getTotalAmount();
        int totalReservedCarsToday = carRentalService.getTotalReservedCarsToday();
        int totalAmountToday = carRentalService.getTotalAmountToday();
        int totalReservedCarsWeek = carRentalService.getTotalReservedCarsWeek();
        int totalAmountWeek = carRentalService.getTotalAmountWeek();

        int totalPayedCars = carRentalService.getTotalPayedCars();
        int totalPayedCarsToday = carRentalService.getTotalPayedCarsToday();
        int totalPayedCarsWeek = carRentalService.getTotalPayedCarsWeek();

        // New statistics for the month
        int totalReservedCarsMonth = carRentalService.getTotalReservedCarsMonth();
        int totalAmountMonth = carRentalService.getTotalAmountMonth();
        int totalPayedCarsMonth = carRentalService.getTotalPayedCarsMonth();

        logger.info("Total Reserved Cars: " + totalReservedCars);
        logger.info("Total Available Cars: " + totalAvailableCars);
        logger.info("Total Maintenance Cars: " + totalMaintenanceCars);
        logger.info("Total Amount: " + totalAmount);
        logger.info("Total Reserved Cars Today: " + totalReservedCarsToday);
        logger.info("Total Amount Today: " + totalAmountToday);
        logger.info("Total Reserved Cars This Week: " + totalReservedCarsWeek);
        logger.info("Total Amount This Week: " + totalAmountWeek);

        model.addAttribute("totalReservedCars", totalReservedCars);
        model.addAttribute("totalAvailableCars", totalAvailableCars);
        model.addAttribute("totalMaintenanceCars", totalMaintenanceCars);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("totalReservedCarsToday", totalReservedCarsToday);
        model.addAttribute("totalAmountToday", totalAmountToday);
        model.addAttribute("totalReservedCarsWeek", totalReservedCarsWeek);
        model.addAttribute("totalAmountWeek", totalAmountWeek);

        model.addAttribute("totalPayedCars", totalPayedCars);
        model.addAttribute("totalPayedCarsToday", totalPayedCarsToday);
        model.addAttribute("totalPayedCarsWeek", totalPayedCarsWeek);

        // Add new attributes for the month
        model.addAttribute("totalReservedCarsMonth", totalReservedCarsMonth);
        model.addAttribute("totalAmountMonth", totalAmountMonth);
        model.addAttribute("totalPayedCarsMonth", totalPayedCarsMonth);

        // Add data for the chart
        List<Integer> totalAmounts = carRentalService.getTotalAmountsOverTime();
        model.addAttribute("totalAmounts", totalAmounts);

        return "statistics";
    }

    @GetMapping("/admin/statistics/download")
    public ResponseEntity<InputStreamResource> downloadStatistics(@RequestParam String period) throws DocumentException, IOException {
        List<Booking> bookings;
        LocalDate now = LocalDate.now();

        switch (period) {
            case "today":
                bookings = bookingService.findAllByDate(now);
                break;
            case "week":
                bookings = bookingService.findAllByDateRange(now.minusDays(7), now);
                break;
            case "month":
                bookings = bookingService.findAllByDateRange(now.minusDays(30), now);
                break;
            case "total":
            default:
                bookings = bookingService.findAll();
                break;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();
        document.add(new Paragraph("Bookings Report - " + period));
        for (Booking booking : bookings) {
            document.add(new Paragraph("Booking ID: " + booking.getBookingId()));
            document.add(new Paragraph("Car ID: " + booking.getCarId()));
            document.add(new Paragraph("Customer ID: " + booking.getCustomerId()));
            document.add(new Paragraph("Booking Date From: " + booking.getBookingDateFrom()));
            document.add(new Paragraph("Booking Date To: " + booking.getBookingDateTo()));
            document.add(new Paragraph("Total Price: " + booking.getTotalPrice()));
            document.add(new Paragraph("Status: " + booking.getStatus()));
            document.add(new Paragraph(" "));
        }
        document.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "bookings_" + period + ".pdf");

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(bis));
    }
}