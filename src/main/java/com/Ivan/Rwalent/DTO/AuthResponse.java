package com.Ivan.Rwalent.DTO;

public class AuthResponse {
    private String email;
    private String token;
    private String message;

    // Constructor
    public AuthResponse(String email, String token, String message) {
        this.email = email;
        this.token = token;
        this.message = message;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 