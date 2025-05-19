package com.example.airline.report.controller;

import com.example.airline.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/manifest/{flightId}")
    // Consider returning as application/pdf
    // @Produces(MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<List<Map<String, String>>> getPassengerManifest(@PathVariable Long flightId) {
        // In a real application, check if flightId exists before generating report
        // For now, assume the service handles cases where no bookings are found for the flight
        List<Map<String, String>> manifestData = reportService.generatePassengerManifest(flightId);

        if (manifestData.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No bookings found for flight ID: " + flightId);
        }

        return ResponseEntity.ok(manifestData);
    }

    @GetMapping("/schedule")
    public ResponseEntity<?> getFlightScheduleReport(
            @RequestParam String airportCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        if (fromDate.isAfter(toDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "From date cannot be after to date.");
        }

        List<?> flights = reportService.generateFlightScheduleReport(airportCode, fromDate, toDate);

        if (flights.isEmpty()) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No flights found for the given criteria.");
        }


        return ResponseEntity.ok(flights);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getBookingStats() {
        Map<String, Object> stats = reportService.generateBookingStats();

        if (stats == null || stats.isEmpty()) {
             return ResponseEntity.status(HttpStatus.NO_CONTENT).body(stats); // Or NOT_FOUND depending on expected behavior
        }

        return ResponseEntity.ok(stats);
    }
}