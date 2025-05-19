package com.example.airline.seat.repository;

import com.example.airline.seat.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByFlightId(Long flightId);

    List<Seat> findByFlightIdAndSeatClass(Long flightId, String seatClass);

    long countByFlightIdAndSeatClassAndBookingStatus(Long flightId, String seatClass, String bookingStatus);
}