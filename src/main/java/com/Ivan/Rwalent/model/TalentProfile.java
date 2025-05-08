package com.Ivan.Rwalent.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "talent_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TalentProfile {

    @Id
    private Long id; // This will be the same as the User ID

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId // Maps the 'id' field to be both PK and FK
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 255)
    private String profileHeadline;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String bio;

    @Enumerated(EnumType.STRING)
    private TalentCategory category;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "talent_sub_categories", joinColumns = @JoinColumn(name = "talent_profile_id"))
    @Column(name = "sub_category")
    private List<String> subCategories = new ArrayList<>();

    private Integer yearsOfExperience;

    @Column(precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    private String location; // e.g., "City, Country"

    private String websiteLink; // Personal website or primary portfolio link

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "talent_portfolio_links", joinColumns = @JoinColumn(name = "talent_profile_id"))
    @Column(name = "link_url")
    private List<String> portfolioLinks = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "talent_skills", joinColumns = @JoinColumn(name = "talent_profile_id"))
    @Column(name = "skill")
    private Set<String> skills = new HashSet<>();

    // Custom constructor if needed, Lombok handles the rest
    public TalentProfile(User user) {
        this.user = user;
    }
} 