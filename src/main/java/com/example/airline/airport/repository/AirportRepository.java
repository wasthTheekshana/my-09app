package com.example.airline.airport.repository;

import com.example.airline.airport.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport, Long> {
}