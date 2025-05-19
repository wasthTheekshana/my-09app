package com.example.airline.flight.service;

import com.example.airline.flight.model.Flight;
import com.example.airline.flight.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    @Autowired
    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Transactional
    public Flight scheduleFlight(Flight flight) {
        // Perform conflict check
        List<Flight> conflictingFlights = flightRepository.findConflictingFlights(
                flight.getAirplaneId(),
                flight.getDepartureTime(),
                flight.getArrivalTime()
        );

        if (!conflictingFlights.isEmpty()) {
            // Handle conflict - e.g., throw a custom exception
            throw new RuntimeException("Flight scheduling conflict with existing flights.");
        }

        // Set initial status
        if (flight.getStatus() == null || flight.getStatus().isEmpty()) {
            flight.setStatus("SCHEDULED");
        }

        return flightRepository.save(flight);
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public List<Flight> searchFlights(String origin, String destination, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return flightRepository.findByOriginAndDestinationAndDepartureTimeBetween(origin, destination, startOfDay, endOfDay);
    }

    public List<List<Flight>> findConnectingFlights(String origin, String destination) {
        List<List<Flight>> connectingRoutes = new ArrayList<>();

        // Find direct flights first
        List<Flight> directFlights = flightRepository.findByOriginAndDestination(origin, destination);
        if (!directFlights.isEmpty()) {
            directFlights.forEach(flight -> {
                List<Flight> route = new ArrayList<>();
                route.add(flight);
                connectingRoutes.add(route);
            });
        }

        // Find one-stop connecting flights
        List<Flight> possibleFirstLegs = flightRepository.findByOrigin(origin);
        for (Flight firstLeg : possibleFirstLegs) {
            List<Flight> possibleSecondLegs = flightRepository.findConnectingFlights(firstLeg.getDestination(), firstLeg.getArrivalTime());
            for (Flight secondLeg : possibleSecondLegs) {
                if (secondLeg.getDestination().equals(destination)) {
                    List<Flight> route = new ArrayList<>();
                    route.add(firstLeg);
                    route.add(secondLeg);
                    connectingRoutes.add(route);
                }
            }
        }

        // TODO: Implement logic for multi-stop flights if needed

        return connectingRoutes;
    }

    public Optional<Flight> getFlightById(Long id) {
        return flightRepository.findById(id);
    }

    @Transactional
    public void deleteFlight(Long id) {
        // Optional: Add checks here to ensure the flight can be deleted (e.g., not booked)
        if (flightRepository.existsById(id)) {
            flightRepository.deleteById(id);
        } else {
            // Handle not found - e.g., throw a custom exception
            throw new RuntimeException("Flight with ID " + id + " not found.");
        }
    }
}