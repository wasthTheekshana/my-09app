package com.example.airline.booking.repository;

import com.example.airline.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    Optional<Booking> findById(Long id);

    List<Booking> findByFlightId(Long flightId);

    List<Booking> findByBookingDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Booking> findByBookingStatus(String bookingStatus);

    // Searching by seat class requires joining with the Seat entity
    // This is a simplified example, actual implementation might vary based on relationship mapping
    @Query("SELECT b FROM Booking b JOIN Seat s ON b.seatId = s.id WHERE s.seatClass = :seatClass")
    List<Booking> findBySeatClass(@Param("seatClass") String seatClass);
}