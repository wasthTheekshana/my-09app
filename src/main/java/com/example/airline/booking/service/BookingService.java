package com.example.airline.booking.service;

import com.example.airline.booking.model.Booking;
import com.example.airline.booking.repository.BookingRepository;
import com.example.airline.booking.client.FlightServiceClient;
import com.example.airline.booking.client.SeatServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FlightServiceClient flightServiceClient;

    @Autowired
    private SeatServiceClient seatServiceClient;

    @Transactional
    public Booking bookSeat(Long userId, Long flightId, String seatNumber, String seatClass) {
        // 1. Call the Flight Service to get flight details
        ResponseEntity<Flight> flightResponse = flightServiceClient.getFlightById(flightId);
        if (!flightResponse.getStatusCode().is2xxSuccessful() || flightResponse.getBody() == null) {
            throw new RuntimeException("Flight not found with ID: " + flightId);
        }
        // Flight flight = flightResponse.getBody(); // If you need flight details

        // 2. Call the Seat Service to get seat details
        // Note: The Seat Service needs an endpoint to find a seat by flight ID and seat number
        // For now, assuming we get all seats for a flight and filter, or assuming SeatService has a findByFlightIdAndSeatNumber endpoint via Feign.
        // Assuming a Feign client method like:
        // @GetMapping("/api/seats/by-flight-seat")
        // ResponseEntity<Seat> findSeatByFlightIdAndSeatNumber(@RequestParam("flightId") Long flightId, @RequestParam("seatNumber") String seatNumber);

        // As a placeholder for now, let's assume seatServiceClient.getSeatById(some_seat_id) works.
        // A better approach would be to find seat ID based on flightId and seatNumber first.
        // Or modify the SeatService to expose a findByFlightIdAndSeatNumber endpoint.
        // For the sake of demonstrating Feign usage here, let's assume a hypothetical seat ID or adjust SeatService FeignClient later.
        // *** This part requires a correct endpoint in the Seat Service and its Feign Client. ***
        // Let's assume we can get the seat ID somehow from the Flight Service response or another endpoint.
        // Placeholder for getting seat details by ID (requires a valid seat ID)
         ResponseEntity<Seat> seatResponse = seatServiceClient.getSeatById(123L); // Replace 123L with actual logic to get seat ID
        if (!seatResponse.getStatusCode().is2xxSuccessful() || seatResponse.getBody() == null) {
            throw new RuntimeException("Seat not found with number " + seatNumber + " for flight ID: " + flightId);
        }

        Seat seat = seatOptional.get();

        if (!"AVAILABLE".equals(seat.getBookingStatus())) {
            throw new RuntimeException("Seat " + seatNumber + " is not available for booking.");
        }

        // Ensure the requested seat class matches the seat's class (optional check)
        if (seatClass != null && !seatClass.equalsIgnoreCase(seat.getSeatClass())) {
             throw new RuntimeException("Seat " + seatNumber + " is in " + seat.getSeatClass() + " class, not " + seatClass);
        }

        // 3. Call the Seat Service to book the seat
        // This assumes the Seat Service's bookSeat endpoint handles the state change.
         ResponseEntity<Void> bookSeatResponse = seatServiceClient.bookSeat(seat.getId()); // Use the actual seat ID
         if (!bookSeatResponse.getStatusCode().is2xxSuccessful()) {
              throw new RuntimeException("Failed to book seat with ID: " + seat.getId());
         }


        // Create the Booking entity
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setFlightId(flightId);
        booking.setSeatId(bookedSeat.getId()); // Set the ID of the booked seat
        booking.setBookingDate(LocalDateTime.now());
        booking.setBookingStatus("CONFIRMED"); // Or "PENDING" if payment is involved

        // Set Price (This is a placeholder, actual price logic would be more complex)
        booking.setPrice(BigDecimal.ZERO); // Placeholder price

        // Save the booking
        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public Optional<Booking> getBookingDetails(Long bookingId) {
        return bookingRepository.findById(bookingId);
    }

    @Transactional
    public Optional<Booking> cancelBooking(Long bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);

        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            if ("CANCELLED".equals(booking.getBookingStatus())) {
                 return Optional.empty(); // Already cancelled
            }

            // Release the seat (using SeatService or Feign Client)
            // Need to call Seat Service to release the seat by its ID or flight/seat number
            // Assuming a Feign client method like:
            // @PutMapping("/api/seats/{id}/release")
            // ResponseEntity<Void> releaseSeat(@PathVariable("id") Long id);

            // For now, let's assume a release endpoint by seat ID exists in SeatService FeignClient
            ResponseEntity<Void> releaseSeatResponse = seatServiceClient.releaseSeat(booking.getSeatId()); // Requires SeatServiceClient.releaseSeat

            if (!releaseSeatResponse.getStatusCode().is2xxSuccessful()) {
                 // Log a warning or handle the failure to release the seat
            }


            // Update booking status
            booking.setBookingStatus("CANCELLED");
            return Optional.of(bookingRepository.save(booking));
        }
        return Optional.empty(); // Booking not found
    }

    public List<Booking> searchBookings(LocalDateTime startDate, LocalDateTime endDate, Long flightId, String seatClass, String bookingStatus) {
        // This method demonstrates combining different search criteria.
        // More complex queries might be needed in the repository or handled with criteria API.
        List<Booking> result = bookingRepository.findAll(); // Start with all or apply initial filter

        if (startDate != null && endDate != null) {
            result = result.stream()
                           .filter(booking -> booking.getBookingDate().isAfter(startDate.minusNanos(1)) && booking.getBookingDate().isBefore(endDate.plusNanos(1)))
                           .collect(Collectors.toList());
        }

        if (flightId != null) {
            result = result.stream()
                           .filter(booking -> booking.getFlightId().equals(flightId))
                           .collect(Collectors.toList());
        }

        if (bookingStatus != null && !bookingStatus.isEmpty()) {
            result = result.stream()
                           .filter(booking -> booking.getBookingStatus().equalsIgnoreCase(bookingStatus))
                           .collect(Collectors.toList());
        }

        // Searching by seat class requires getting seat information, potentially using SeatService
        if (seatClass != null && !seatClass.isEmpty()) {
             result = result.stream()
                           .filter(booking -> {
                               // To filter by seat class, we need to call the Seat Service to get seat details
                               ResponseEntity<Seat> seatResponse = seatServiceClient.getSeatById(booking.getSeatId()); // Use SeatServiceClient
                                return seatOptional.isPresent() && seatOptional.get().getSeatClass().equalsIgnoreCase(seatClass);
                           })
                           .collect(Collectors.toList());
        }


        return result;
    }
}