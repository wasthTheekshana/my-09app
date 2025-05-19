package com.example.airline.auth.controller;

import com.example.airline.auth.dto.AuthResponseDto;
import com.example.airline.auth.dto.LoginDto;
import com.example.airline.auth.dto.SignupDto;
import com.example.airline.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RestController

@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    // Basic in-memory rate limiting for login attempts per IP
    private final ConcurrentMap<String, LoginAttempt> loginAttemptCache = new ConcurrentHashMap<>();
    private static final int MAX_LOGIN_ATTEMPTS = 5; // Maximum allowed attempts
    private static final long LOGIN_ATTEMPT_RESET_TIME_MILLIS = 60 * 1000; // Reset after 1 minute

    public ResponseEntity<?> signup(@RequestBody SignupDto signupDto) {
        try {
            AuthResponseDto authResponse = authService.signup(signupDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
        } catch (RuntimeException e) {
            // Handle specific exceptions from service (e.g., EmailAlreadyExistsException)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr(); // Get client IP address

        // Check and apply rate limiting
        LoginAttempt attempt = loginAttemptCache.computeIfAbsent(clientIp, k -> new LoginAttempt());

        if (attempt.attempts >= MAX_LOGIN_ATTEMPTS && System.currentTimeMillis() - attempt.timestamp < LOGIN_ATTEMPT_RESET_TIME_MILLIS) {
            HttpHeaders headers = new HttpHeaders();
            long timeLeft = LOGIN_ATTEMPT_RESET_TIME_MILLIS - (System.currentTimeMillis() - attempt.timestamp);
            headers.add("Retry-After", String.valueOf(timeLeft / 1000)); // Provide retry information
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).headers(headers).body("Too many login attempts. Please try again later.");
        }

        // Reset attempt count if reset time has passed
        if (System.currentTimeMillis() - attempt.timestamp >= LOGIN_ATTEMPT_RESET_TIME_MILLIS) {
            attempt.reset();
        }

        try {
            AuthResponseDto authResponse = authService.login(loginDto);
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            // Handle specific exceptions from service (e.g., InvalidCredentialsException)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/access-token/refresh-token/{refreshToken}")
    public ResponseEntity<?> refreshAccessToken(@PathVariable String refreshToken) {
        try {
            AuthResponseDto authResponse = authService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            // Handle specific exceptions from service (e.g., InvalidTokenException)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // Helper class to track login attempts
    private static class LoginAttempt {
        private int attempts;
        private long timestamp;

        public LoginAttempt() {
            reset();
        }

        public void reset() {
            this.attempts = 0;
            this.timestamp = System.currentTimeMillis();
        }
    }
}