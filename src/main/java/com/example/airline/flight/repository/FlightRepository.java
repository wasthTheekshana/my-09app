package com.example.airline.flight.repository;

import com.example.airline.flight.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findByOriginAndDestinationAndDepartureTimeBetween(String origin, String destination, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Flight> findByOriginAndDestination(String origin, String destination);

    // Method to find potential connecting flights
    // Finds flights departing from a given airport after a specific time
    List<Flight> findByOriginAndDepartureTimeAfter(String origin, LocalDateTime departureTime);

    // Custom query example for more complex connecting flight logic if needed
    // This is a basic example, more sophisticated logic would likely be in the service layer
    @Query("SELECT f FROM Flight f WHERE f.origin = :origin AND f.departureTime > :arrivalTime")
    List<Flight> findConnectingFlightsFromAirportAfterTime(@Param("origin") String origin, @Param("arrivalTime") LocalDateTime arrivalTime);

    Optional<Flight> findByFlightNumber(String flightNumber);
}