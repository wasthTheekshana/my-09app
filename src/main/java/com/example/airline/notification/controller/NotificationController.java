package com.example.airline.notification.controller;

import com.example.airline.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notify")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/booking")
    public ResponseEntity<String> sendBookingConfirmation(@RequestParam Long bookingId) {
        if (bookingId == null) {
            return ResponseEntity.badRequest().body("Booking ID is required.");
        }

        boolean sent = notificationService.sendBookingConfirmation(bookingId);

        if (sent) {
            return ResponseEntity.ok("Booking confirmation sent successfully.");
        } else {
            // More specific error message could be returned based on service feedback
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send booking confirmation.");
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> sendCancellationMessage(@RequestParam Long bookingId) {
         if (bookingId == null) {
            return ResponseEntity.badRequest().body("Booking ID is required.");
        }

        boolean sent = notificationService.sendCancellationMessage(bookingId);

        if (sent) {
            return ResponseEntity.ok("Cancellation message sent successfully.");
        } else {
            // More specific error message could be returned based on service feedback
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send cancellation message.");
        }
    }
}