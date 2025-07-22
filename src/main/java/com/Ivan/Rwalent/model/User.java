package com.Ivan.Rwalent.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String fullName;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = true)
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = true, columnDefinition = "VARCHAR(50)")
    private TalentCategory category;
    
    @Column(nullable = true)
    private String location;
    
    @Column(nullable = true, length = 1000)
    private String bio;
    
    @Column(nullable = true, length = 1000)
    private String serviceAndPricing;
    
    @Column(nullable = true)
    private String photoUrl;
    
    @Column(name = "profile_picture")
    private String profilePicture;
    
    @Column(nullable = false)
    private boolean enabled = true;
    
    @Column(name = "profile_views", nullable = false)
    private int profileViews = 0;
    
    public enum UserType {
        REGULAR, TALENT
    }
    
    public enum TalentCategory {
        OTHER,
        MUSICIAN,
        WRITER,
        ACTOR_ACTRESS,
        ACTOR,
        ACTRESS,
        VISUAL_ARTIST,
        PHOTOGRAPHER,
        TUTORING,
        DANCER,
        DJ,
        MODEL,
        CHEF,
        COMEDIAN;

        @JsonValue
        public String toValue() {
            return this.name();
        }

        @JsonCreator
        public static TalentCategory fromValue(String value) {
            if (value == null) {
                return null;
            }
            String normalizedValue = value.toUpperCase().replace("/", "_");
            try {
                return valueOf(normalizedValue);
            } catch (IllegalArgumentException e) {
                // Handle special cases
                if (normalizedValue.contains("ACTOR") || normalizedValue.contains("ACTRESS")) {
                    return ACTOR_ACTRESS;
                }
                return OTHER; // Default to OTHER if value doesn't match any enum
            }
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public TalentCategory getCategory() {
        return category;
    }

    public void setCategory(TalentCategory category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getServiceAndPricing() {
        return serviceAndPricing;
    }

    public void setServiceAndPricing(String serviceAndPricing) {
        this.serviceAndPricing = serviceAndPricing;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getProfileViews() {
        return profileViews;
    }

    public void setProfileViews(int profileViews) {
        this.profileViews = profileViews;
    }
}
