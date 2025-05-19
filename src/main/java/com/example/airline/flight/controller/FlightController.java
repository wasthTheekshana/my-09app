package com.example.airline.flight.controller;

import com.example.airline.flight.model.Flight;
import com.example.airline.flight.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @PostMapping
    public ResponseEntity<?> scheduleFlight(@RequestBody Flight flight) {
        try {
            Flight scheduledFlight = flightService.scheduleFlight(flight);
            return ResponseEntity.status(HttpStatus.CREATED).body(scheduledFlight);
        } catch (RuntimeException e) {
            // Handle scheduling conflict or other errors
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Flight>> getAllFlights() {
        List<Flight> flights = flightService.getAllFlights();
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchFlights(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String date) {
        try {
            LocalDate searchDate = LocalDate.parse(date);
            List<Flight> flights = flightService.searchFlights(origin, destination, searchDate);
            if (flights.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content if no flights found
            }
            return ResponseEntity.ok(flights);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format. Please use YYYY-MM-DD.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while searching for flights.");
        }
    }

    @GetMapping("/transit")
    public ResponseEntity<?> findConnectingFlights(
            @RequestParam String origin,
            @RequestParam String destination) {
        try {
            List<List<Flight>> connectingFlights = flightService.findConnectingFlights(origin, destination);
             if (connectingFlights.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content if no connecting flights found
            }
            return ResponseEntity.ok(connectingFlights);
        } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while searching for connecting flights.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flight> getFlightById(@PathVariable Long id) {
        Optional<Flight> flight = flightService.getFlightById(id);
        return flight.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        Optional<Flight> flight = flightService.getFlightById(id);
        if (flight.isPresent()) {
            flightService.deleteFlight(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}