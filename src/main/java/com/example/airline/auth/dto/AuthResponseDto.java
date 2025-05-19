package com.example.airline.auth.dto;

import com.example.airline.user.model.User; // Or a simplified user DTO

public class AuthResponseDto {

    private String accessToken;
    private String refreshToken;
    private User user; // Or a simplified user DTO with relevant info

    // Getters and Setters

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}