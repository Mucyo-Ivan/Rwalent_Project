package com.Ivan.Rwalent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private String fullName;
    private String userType;

    public JwtResponse(String token, String email, String fullName, String userType) {
        this.token = token;
        this.email = email;
        this.fullName = fullName;
        this.userType = userType;
    }
} 