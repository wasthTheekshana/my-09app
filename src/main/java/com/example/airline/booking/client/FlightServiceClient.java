package com.example.airline.booking.client;

import com.example.airline.flight.model.Flight;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "flight-service") // Name of the target Flight Service
public interface FlightServiceClient {

    @GetMapping("/api/flights/{id}")
    ResponseEntity<Flight> getFlightById(@PathVariable("id") Long id);

    // Add other method signatures for Flight Service endpoints needed by Booking Service
    // For example:
    // @GetMapping("/api/flights/search")
    // ResponseEntity<List<Flight>> searchFlights(@RequestParam("origin") String origin, ...);
}