package com.example.airline.booking.controller;

import com.example.airline.booking.model.Booking;
import com.example.airline.booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> bookSeat(@RequestBody BookingRequest bookingRequest) {
        // In a real application, you would get the userId from the authenticated principal
        // For now, assuming userId is part of the request body or retrieved otherwise
        Long userId = bookingRequest.getUserId(); // Or retrieve from SecurityContextHolder

        Optional<Booking> booking = bookingService.bookSeat(
                userId,
                bookingRequest.getFlightId(),
                bookingRequest.getSeatNumber()
        );

        if (booking.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(booking.get());
        } else {
            // More specific error handling can be added in the service or here
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not book seat. Flight not found, seat not available, or other issue.");
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<Booking>> getUserBookings() {
        // Get the authenticated user's ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null; // You need a way to get the user ID from the principal
                           // This depends on your UserDetails implementation

        // Example if your UserDetails has a method to get the ID
        /*
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            // Assuming your UserDetails implementation has getUserId()
            userId = ((com.example.airline.user.security.UserDetailsImpl) principal).getUserId();
        } else {
             // Handle cases where the principal is not UserDetails
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        */

        // For now, a placeholder assuming you can get the userId
         if (userId == null) {
             // This part needs proper implementation based on your auth setup
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         }


        List<Booking> userBookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(userBookings);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBookingDetails(@PathVariable Long bookingId) {
        Optional<Booking> booking = bookingService.getBookingDetails(bookingId);
        return booking.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        Optional<Booking> cancelledBooking = bookingService.cancelBooking(bookingId);
        if (cancelledBooking.isPresent()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build(); // Booking not found or already cancelled
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Booking>> searchBookings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String seatClass,
            @RequestParam(required = false) Long flightId,
            @RequestParam(required = false) String status) {

        List<Booking> searchResults = bookingService.searchBookings(
                Optional.ofNullable(date),
                Optional.ofNullable(seatClass),
                Optional.ofNullable(flightId),
                Optional.ofNullable(status)
        );
        return ResponseEntity.ok(searchResults);
    }

    // Helper class for booking request body
    static class BookingRequest {
        private Long userId; // Temporal, should be retrieved from auth
        private Long flightId;
        private String seatNumber;
        private String seatClass; // Might be needed for validation or price calculation

        // Getters and Setters

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getFlightId() {
            return flightId;
        }

        public void setFlightId(Long flightId) {
            this.flightId = flightId;
        }

        public String getSeatNumber() {
            return seatNumber;
        }

        public void setSeatNumber(String seatNumber) {
            this.seatNumber = seatNumber;
        }

        public String getSeatClass() {
            return seatClass;
        }

        public void setSeatClass(String seatClass) {
            this.seatClass = seatClass;
        }
    }
}