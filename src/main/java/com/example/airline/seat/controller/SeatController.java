package com.example.airline.seat.controller;

import com.example.airline.seat.model.Seat;
import com.example.airline.seat.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping("/availability")
    public ResponseEntity<Integer> getAvailableSeats(
            @RequestParam Long flightId,
            @RequestParam String seatClass) {
        int availableSeats = seatService.getAvailableSeats(flightId, seatClass);
        return ResponseEntity.ok(availableSeats);
    }

    @GetMapping("/map/{flightId}")
    public ResponseEntity<List<Seat>> getSeatMap(@PathVariable Long flightId) {
        List<Seat> seatMap = seatService.getSeatMap(flightId);
        if (seatMap.isEmpty()) {
            // Depending on requirements, you might want to check if the flight exists first
            // For now, returning an empty list for a non-existent flight ID is acceptable based on service logic
            return ResponseEntity.ok(seatMap);
        }
        return ResponseEntity.ok(seatMap);
    }
}