package com.example.airline.seat.service;

import com.example.airline.seat.model.Seat;
import com.example.airline.seat.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    public int getAvailableSeats(Long flightId, String seatClass) {
        return seatRepository.countByFlightIdAndSeatClassAndBookingStatus(flightId, seatClass, "AVAILABLE");
    }

    public List<Seat> getSeatMap(Long flightId) {
        return seatRepository.findByFlightId(flightId);
    }

    public Seat bookSeat(Long flightId, String seatNumber) {
        Optional<Seat> seatOptional = seatRepository.findByFlightIdAndSeatNumber(flightId, seatNumber);

        if (seatOptional.isPresent()) {
            Seat seat = seatOptional.get();
            if ("AVAILABLE".equals(seat.getBookingStatus())) {
                seat.setBookingStatus("BOOKED");
                return seatRepository.save(seat);
            } else {
                throw new IllegalStateException("Seat " + seatNumber + " is already booked.");
            }
        } else {
            throw new EntityNotFoundException("Seat with number " + seatNumber + " not found for flight ID " + flightId);
        }
    }

    public Seat releaseSeat(Long flightId, String seatNumber) {
        Optional<Seat> seatOptional = seatRepository.findByFlightIdAndSeatNumber(flightId, seatNumber);

        if (seatOptional.isPresent()) {
            Seat seat = seatOptional.get();
            if ("BOOKED".equals(seat.getBookingStatus())) {
                seat.setBookingStatus("AVAILABLE");
                return seatRepository.save(seat);
            } else {
                // Seat is already available or in another status, might log or handle differently
                 return seat; // Or throw an exception if releasing an available seat is an error
            }
        } else {
            throw new EntityNotFoundException("Seat with number " + seatNumber + " not found for flight ID " + flightId);
        }
    }

    // Method to initialize seats for a new flight (to be called when a flight is scheduled)
    public void createSeatsForFlight(Long flightId, int economySeats, int businessSeats, int firstClassSeats) {
        // This is a simplified example. In a real app, you'd have a more sophisticated way
        // to generate seat numbers based on plane configuration.
        for (int i = 1; i <= economySeats; i++) {
            Seat seat = new Seat();
            seat.setFlightId(flightId);
            seat.setSeatNumber("E" + i); // Example seat numbering
            seat.setSeatClass("ECONOMY");
            seat.setBookingStatus("AVAILABLE");
            seatRepository.save(seat);
        }
        for (int i = 1; i <= businessSeats; i++) {
            Seat seat = new Seat();
            seat.setFlightId(flightId);
            seat.setSeatNumber("B" + i); // Example seat numbering
            seat.setSeatClass("BUSINESS");
            seat.setBookingStatus("AVAILABLE");
            seatRepository.save(seat);
        }
         for (int i = 1; i <= firstClassSeats; i++) {
            Seat seat = new Seat();
            seat.setFlightId(flightId);
            seat.setSeatNumber("F" + i); // Example seat numbering
            seat.setSeatClass("FIRST");
            seat.setBookingStatus("AVAILABLE");
            seatRepository.save(seat);
        }
    }
}