package com.Ivan.Rwalent.dto;

import com.Ivan.Rwalent.model.TalentCategory;
import com.Ivan.Rwalent.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TalentProfileDTO {
    // From User entity
    private Long id; // User ID
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String photoUrl;
    private User.UserType userType;

    // From TalentProfile entity
    private String profileHeadline;
    private String bio;
    private TalentCategory category;
    private List<String> subCategories;
    private Integer yearsOfExperience;
    private BigDecimal hourlyRate;
    private String location;
    private String websiteLink;
    private List<String> portfolioLinks;
    private Set<String> skills;
    
    // lastLogin and createdAt from User can be added if needed for display
} 