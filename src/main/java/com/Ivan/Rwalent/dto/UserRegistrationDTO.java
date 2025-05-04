package com.Ivan.Rwalent.dto;

import com.Ivan.Rwalent.model.User.TalentCategory;
import com.Ivan.Rwalent.model.User.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDTO {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    // Optional fields for talent users
    private String phoneNumber;
    private TalentCategory category;
    private String location;
    private String bio;
    private String serviceAndPricing;
    private String photoUrl;
    private UserType userType = UserType.REGULAR; // Default to regular user
} 