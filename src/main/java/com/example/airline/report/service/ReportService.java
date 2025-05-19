package com.example.airline.report.service;

import com.example.airline.booking.model.Booking;
import com.example.airline.flight.model.Flight;
import com.example.airline.user.model.User;
import com.example.airline.booking.service.BookingService; // Placeholder, use Feign Client
import com.example.airline.flight.service.FlightService; // Placeholder, use Feign Client
import com.example.airline.user.service.UserService; // Placeholder, use Feign Client


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    // @Autowired
    // private BookingFeignClient bookingFeignClient; // Use actual Feign Client
    // @Autowired
    // private FlightFeignClient flightFeignClient; // Use actual Feign Client
    // @Autowired
    // private UserFeignClient userFeignClient; // Use actual Feign Client

    // Placeholder Services for demonstration without Feign Clients
    @Autowired
    private BookingService bookingService;
    @Autowired
    private FlightService flightService;
    @Autowired
    private UserService userService;


    public List<PassengerManifestEntry> generatePassengerManifest(Long flightId) {
        List<PassengerManifestEntry> manifest = new ArrayList<>();

        // Get bookings for the flight from Booking Service (via Feign Client)
        // List<Booking> bookings = bookingFeignClient.getBookingsByFlightId(flightId);
        List<Booking> bookings = bookingService.findByFlightId(flightId); // Placeholder call


        for (Booking booking : bookings) {
            // Get user details from User Service (via Feign Client)
            // Optional<User> userOptional = userFeignClient.getUserById(booking.getUserId());
            Optional<User> userOptional = userService.findById(booking.getUserId()); // Placeholder call

            // Get seat details from Seat Service (via Feign Client) - not explicitly required by prompt, but useful
            // Optional<Seat> seatOptional = seatFeignClient.getSeatById(booking.getSeatId());


            if (userOptional.isPresent()) {
                User user = userOptional.get();
                PassengerManifestEntry entry = new PassengerManifestEntry();
                // entry.setSeatNumber(seatOptional.map(Seat::getSeatNumber).orElse("N/A")); // If using Seat Service
                entry.setPassengerName(user.getUsername()); // Using username as passenger name for now
                entry.setBookingId(booking.getId());
                // Add other relevant details like seat number, class if available
                manifest.add(entry);
            }
        }

        return manifest; // Data structured for PDF generation
    }

    public List<Flight> generateFlightScheduleReport(String airportCode, LocalDate fromDate, LocalDate toDate) {
        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime = toDate.atTime(LocalTime.MAX);

        // Get flights from Flight Service (via Feign Client) based on origin or destination and date range
        // List<Flight> arrivalFlights = flightFeignClient.getFlightsByDestinationAndDateRange(airportCode, startDateTime, endDateTime);
        // List<Flight> departureFlights = flightFeignClient.getFlightsByOriginAndDateRange(airportCode, startDateTime, endDateTime);

        // Placeholder calls (assuming findByOrigin and findByDestination with date range exist)
         List<Flight> arrivalFlights = flightService.findByDestinationAndDepartureTimeBetween(airportCode, startDateTime, endDateTime);
         List<Flight> departureFlights = flightService.findByOriginAndDepartureTimeBetween(airportCode, startDateTime, endDateTime);


        List<Flight> scheduleReport = new ArrayList<>();
        scheduleReport.addAll(arrivalFlights);
        scheduleReport.addAll(departureFlights);

        // You might want to sort these flights by time

        return scheduleReport;
    }

    public BookingStats generateBookingStatistics() {
        // Get all bookings from Booking Service (via Feign Client)
        // List<Booking> allBookings = bookingFeignClient.getAllBookings();
        List<Booking> allBookings = bookingService.getAllBookings(); // Placeholder call


        BookingStats stats = new BookingStats();

        // Booking Trends (e.g., bookings per day/month)
        Map<LocalDate, Long> bookingsByDate = allBookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getBookingDate().toLocalDate(), Collectors.counting()));
        stats.setBookingTrends(bookingsByDate);

        // Top Destinations (requires getting Flight info from Flight Service via Feign Client)
        Map<String, Long> bookingsByDestination = allBookings.stream()
                 .collect(Collectors.groupingBy(booking -> {
                     // You would get flight details here to find the destination
                     // Optional<Flight> flightOptional = flightFeignClient.getFlightById(booking.getFlightId());
                     // return flightOptional.map(Flight::getDestination).orElse("Unknown");
                     // Placeholder: assuming Flight object is retrieved somewhere
                     return "Placeholder Destination";
                 }, Collectors.counting()));
         stats.setTopDestinations(bookingsByDestination);


        // Class-wise Occupancy (requires getting Seat info from Seat Service via Feign Client)
        Map<String, Long> bookingsBySeatClass = allBookings.stream()
                 .collect(Collectors.groupingBy(booking -> {
                     // You would get seat details here to find the seat class
                     // Optional<Seat> seatOptional = seatFeignClient.getSeatById(booking.getSeatId());
                     // return seatOptional.map(Seat::getSeatClass).orElse("Unknown");
                      return "Placeholder Class";
                 }, Collectors.counting()));
         stats.setClassWiseOccupancy(bookingsBySeatClass);


        return stats;
    }

    // Helper class to structure data for passenger manifest
    public static class PassengerManifestEntry {
        private Long bookingId;
        private String passengerName;
        private String seatNumber; // Add if you retrieve seat details

        public Long getBookingId() {
            return bookingId;
        }

        public void setBookingId(Long bookingId) {
            this.bookingId = bookingId;
        }

        public String getPassengerName() {
            return passengerName;
        }

        public void setPassengerName(String passengerName) {
            this.passengerName = passengerName;
        }

        public String getSeatNumber() {
            return seatNumber;
        }

        public void setSeatNumber(String seatNumber) {
            this.seatNumber = seatNumber;
        }
    }

    // Helper class to structure booking statistics
    public static class BookingStats {
        private Map<LocalDate, Long> bookingTrends;
        private Map<String, Long> topDestinations;
        private Map<String, Long> classWiseOccupancy;

        public Map<LocalDate, Long> getBookingTrends() {
            return bookingTrends;
        }

        public void setBookingTrends(Map<LocalDate, Long> bookingTrends) {
            this.bookingTrends = bookingTrends;
        }

        public Map<String, Long> getTopDestinations() {
            return topDestinations;
        }

        public void setTopDestinations(Map<String, Long> topDestinations) {
            this.topDestinations = topDestinations;
        }

        public Map<String, Long> getClassWiseOccupancy() {
            return classWiseOccupancy;
        }

        public void setClassWiseOccupancy(Map<String, Long> classWiseOccupancy) {
            this.classWiseOccupancy = classWiseOccupancy;
        }
    }
}