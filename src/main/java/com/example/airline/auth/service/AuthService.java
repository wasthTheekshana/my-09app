package com.example.airline.auth.service;

import com.example.airline.auth.dto.AuthResponseDto;
import com.example.airline.auth.dto.LoginDto;
import com.example.airline.auth.dto.SignupDto;
import com.example.airline.user.model.User;
import com.example.airline.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthService {

    // @Autowired
    private UserRepository userRepository;

    // @Autowired
    // private PasswordEncoder passwordEncoder; // Inject actual PasswordEncoder or use a bean

    // @Autowired
    // private JwtUtility jwtUtility; // Inject actual JWT Utility class or use a bean

    // @Autowired
    // private AuthenticationManager authenticationManager; // Inject actual AuthenticationManager or use a bean
    

    // Placeholder implementations for security components
    private PasswordEncoder passwordEncoder = new PlaceholderPasswordEncoder();
    private JwtUtility jwtUtility = new PlaceholderJwtUtility();
    private AuthenticationManager authenticationManager = new PlaceholderAuthenticationManager();


    public AuthResponseDto signup(SignupDto signupDto) {
        if (userRepository.findByEmail(signupDto.getEmail()).isPresent()) {
 throw new RuntimeException("Email already exists"); // Use a more specific exception
        }

        User user = new User();
        user.setEmail(signupDto.getEmail());
        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        user.setRoles(signupDto.getRoles().stream().collect(Collectors.joining(",")));

        // Generate initial refresh token upon signup
        String initialRefreshToken = jwtUtility.generateRefreshToken(user.getEmail());
        user.setRefreshToken(initialRefreshToken); // Store refresh token in User entity

        User savedUser = userRepository.save(user);
        String accessToken = jwtUtility.generateAccessToken(savedUser.getEmail());
        String refreshToken = jwtUtility.generateRefreshToken(savedUser.getEmail());

        AuthResponseDto authResponse = new AuthResponseDto();
        authResponse.setAccessToken(accessToken);
        authResponse.setRefreshToken(refreshToken);
        authResponse.setUser(savedUser); // Or a simplified user DTO

        return authResponse;
    }
    

    public AuthResponseDto login(LoginDto loginDto) {
        try {
             authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
            // SecurityContextHolder.getContext().setAuthentication(authentication); // Set in filter
            Optional<User> userOptional = userRepository.findByEmail(loginDto.getEmail());
            if (!userOptional.isPresent()) {
 throw new RuntimeException("User not found after authentication"); // Should not happen if authentication succeeds
            }
            User user = userOptional.get();
            String accessToken = jwtUtility.generateAccessToken(user.getEmail());
            String refreshToken = jwtUtility.generateRefreshToken(user.getEmail());
            user.setRefreshToken(refreshToken); // Store the new refresh token
            userRepository.save(user);

            AuthResponseDto authResponse = new AuthResponseDto();
            authResponse.setAccessToken(accessToken);
            authResponse.setRefreshToken(refreshToken);
            authResponse.setUser(user); // Or a simplified user DTO
            return authResponse;

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password"); // Use a more specific exception
        }
    }

    public String refreshAccessToken(String refreshToken) {
        // 1. Validate the incoming refresh token
        if (jwtUtility.validateToken(refreshToken)) {
            String email = jwtUtility.getEmailFromToken(refreshToken);
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (!userOptional.isPresent()) {
 throw new RuntimeException("User not found");
            }
            User user = userOptional.get();

            // 2. Check if the refresh token is still valid in the database and hasn't been used before
            if (user.getRefreshToken() != null && user.getRefreshToken().equals(refreshToken)) {
                // 3. Invalidate the old refresh token in the database (implicitly done by replacing)

                // 4. Generate a *new* access token and a *new* refresh token
                String newAccessToken = jwtUtility.generateAccessToken(email);
                String newRefreshToken = jwtUtility.generateRefreshToken(email);

                // 5. Store the new refresh token in the database
                user.setRefreshToken(newRefreshToken);
                userRepository.save(user);

                // 6. Return an AuthResponseDto containing the new access token and the new refresh token
                AuthResponseDto authResponseDto = new AuthResponseDto();
                authResponseDto.setAccessToken(newAccessToken);
                authResponseDto.setRefreshToken(newRefreshToken);
                authResponseDto.setUser(user); // Include user info
                return authResponseDto;
            } else {
 throw new RuntimeException("Invalid or used refresh token"); // Or a custom exception
            }
        } else {
 throw new RuntimeException("Invalid refresh token format"); // Or a custom exception
        }
    }

    // Placeholder implementations for security components

    public AuthResponseDto refreshAccessToken(String refreshToken) {
        // Placeholder logic - replace with actual JWT validation and refresh token handling
        if ("fakeRefreshToken".equals(refreshToken)) {
            AuthResponseDto response = new AuthResponseDto();
            response.setAccessToken("newFakeAccessToken");
            response.setRefreshToken("newFakeRefreshToken"); // Rotate refresh token as well
            return response;
        } else {
    private static class PlaceholderPasswordEncoder implements PasswordEncoder {
        @Override
        public String encode(CharSequence rawPassword) {
            System.out.println("PlaceholderPasswordEncoder: encoding password");
            return "hashed_" + rawPassword; // Simple placeholder hashing
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            System.out.println("PlaceholderPasswordEncoder: matching password");
            return encodedPassword != null && encodedPassword.equals("hashed_" + rawPassword); // Simple placeholder matching
        }
    }

    private static class PlaceholderJwtUtility {
        public String generateAccessToken(String email) {
            System.out.println("PlaceholderJwtUtility: generating access token for " + email);
            return "access_token_for_" + email; // Simple placeholder token
        }

        public String generateRefreshToken(String email) {
            System.out.println("PlaceholderJwtUtility: generating refresh token for " + email);
            return "refresh_token_for_" + email; // Simple placeholder token
        }

        public boolean validateToken(String token) {
            System.out.println("PlaceholderJwtUtility: validating token " + token);
            return token != null && (token.startsWith("access_token_for_") || token.startsWith("refresh_token_for_")); // Simple placeholder validation
        }

        public String getEmailFromToken(String token) {
            System.out.println("PlaceholderJwtUtility: getting email from token " + token);
            if (token != null && token.startsWith("access_token_for_")) {
                return token.substring("access_token_for_".length());
            } else if (token != null && token.startsWith("refresh_token_for_")) {
                 return token.substring("refresh_token_for_".length());
            }
            return null; // Or throw an exception
        }
    }

    private static class PlaceholderAuthenticationManager implements AuthenticationManager {
        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            System.out.println("PlaceholderAuthenticationManager: authenticating user");
            // In a real implementation, this would use UserDetailsService and PasswordEncoder
            // to verify credentials.
            String email = authentication.getName();
            String password = authentication.getCredentials().toString();
            System.out.println("Attempting to authenticate with email: " + email + ", password: " + password);
            // Simulate successful authentication
            if ("test@example.com".equals(email) && "password".equals(password)) {
                 return new UsernamePasswordAuthenticationToken(email, password, null); // Return authenticated token
            }
            throw new RuntimeException("Invalid credentials (Placeholder)"); // Simulate authentication failure
        }
    }
}