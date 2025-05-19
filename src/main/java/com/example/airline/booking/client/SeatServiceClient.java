package com.example.airline.booking.client;

import com.example.airline.seat.model.Seat; // Assuming Seat model is available
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "seat-service") // Name of the target service
public interface SeatServiceClient {

    @GetMapping("/api/seats/{id}")
    ResponseEntity<Seat> getSeatById(@PathVariable("id") Long id);

    @PutMapping("/api/seats/{id}/book")
    ResponseEntity<Void> bookSeat(@PathVariable("id") Long id);

    // Add other methods for Seat Service endpoints as needed by Booking Service
    // For example:
    // @PutMapping("/api/seats/{id}/release")
    // ResponseEntity<Void> releaseSeat(@PathVariable("id") Long id);
}