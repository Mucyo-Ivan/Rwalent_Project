package com.Ivan.Rwalent.service.impl;

import com.Ivan.Rwalent.dto.TalentProfileDTO;
import com.Ivan.Rwalent.exception.NotATalentException;
import com.Ivan.Rwalent.exception.UserNotFoundException;
import com.Ivan.Rwalent.model.TalentProfile;
import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.repository.TalentProfileRepository;
import com.Ivan.Rwalent.repository.UserRepository;
import com.Ivan.Rwalent.service.TalentProfileService;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Service
public class TalentProfileServiceImpl implements TalentProfileService {

    private static final Logger logger = LoggerFactory.getLogger(TalentProfileServiceImpl.class);
    private final UserRepository userRepository;
    private final TalentProfileRepository talentProfileRepository;
    private final EntityManager entityManager;

    @Autowired
    public TalentProfileServiceImpl(UserRepository userRepository, TalentProfileRepository talentProfileRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.talentProfileRepository = talentProfileRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public TalentProfileDTO getTalentProfile(User talentUser) {
        if (talentUser.getUserType() != User.UserType.TALENT) {
            throw new NotATalentException("User with ID " + talentUser.getId() + " is not a talent.");
        }

        Assert.notNull(talentUser.getId(), "Incoming Talent User ID cannot be null when fetching or creating TalentProfile.");

        User managedTalentUser = userRepository.findById(talentUser.getId())
                .orElseThrow(() -> new UserNotFoundException("Talent user with ID " + talentUser.getId() + " not found. Profile cannot be retrieved or created."));

        logger.info("Attempting to fetch or create TalentProfile for managed User ID: {}", managedTalentUser.getId());

        Optional<TalentProfile> existingProfileOpt = talentProfileRepository.findByUserId(managedTalentUser.getId());

        TalentProfile talentProfile;
        if (existingProfileOpt.isPresent()) {
            talentProfile = existingProfileOpt.get();
            logger.info("Found existing TalentProfile for User ID: {}. Profile ID: {}", managedTalentUser.getId(), talentProfile.getId());
            logger.debug("Existing TalentProfile - Headline: '{}', Bio: '{}', Category: {}, SubCategories: {}, Experience: {}, HourlyRate: {}, Location: '{}', Website: '{}', PortfolioLinks: {}, Skills: {}",
                talentProfile.getProfileHeadline(),
                talentProfile.getBio() != null ? talentProfile.getBio().substring(0, Math.min(talentProfile.getBio().length(), 50)) + "..." : "null",
                talentProfile.getCategory(),
                talentProfile.getSubCategories() != null ? talentProfile.getSubCategories().size() : "null collection",
                talentProfile.getYearsOfExperience(),
                talentProfile.getHourlyRate(),
                talentProfile.getLocation(),
                talentProfile.getWebsiteLink(),
                talentProfile.getPortfolioLinks() != null ? talentProfile.getPortfolioLinks().size() : "null collection",
                talentProfile.getSkills() != null ? talentProfile.getSkills().size() : "null collection"
            );
        } else {
            logger.info("No TalentProfile found for User ID: {}. Creating a new one.", managedTalentUser.getId());
            Assert.notNull(managedTalentUser.getId(), "Managed Talent User ID is null just before TalentProfile constructor call.");
            
            TalentProfile newProfile = new TalentProfile(managedTalentUser);
            logger.info("New TalentProfile object created (ID will be set on save from User ID: {}). Initializing collections and saving.", managedTalentUser.getId());

            newProfile.setSubCategories(new ArrayList<>());
            newProfile.setPortfolioLinks(new ArrayList<>());
            newProfile.setSkills(new HashSet<>());
            
            try {
                talentProfile = talentProfileRepository.save(newProfile);
                logger.info("Successfully saved new TalentProfile with ID: {} for User ID: {}", talentProfile.getId(), managedTalentUser.getId());
            } catch (Exception e) {
                logger.error("Error saving new TalentProfile for User ID: {}. Exception: {}", 
                             managedTalentUser.getId(), e.getMessage(), e);
                throw e; 
            }
        }

        return convertToDTO(managedTalentUser, talentProfile);
    }

    @Override
    @Transactional
    public TalentProfileDTO updateTalentProfile(User talentUser, TalentProfileDTO talentProfileDTO) {
        if (talentUser.getUserType() != User.UserType.TALENT) {
            throw new NotATalentException("User with ID " + talentUser.getId() + " is not a talent and cannot update a talent profile.");
        }
        Assert.notNull(talentUser.getId(), "Incoming Talent User ID cannot be null for profile update.");

        User managedTalentUser = userRepository.findById(talentUser.getId())
            .orElseThrow(() -> new UserNotFoundException("Talent user with ID " + talentUser.getId() + " not found. Profile cannot be updated."));

        String newFullName = ((talentProfileDTO.getFirstName() != null ? talentProfileDTO.getFirstName().trim() : "") + " " +
                              (talentProfileDTO.getLastName() != null ? talentProfileDTO.getLastName().trim() : "")).trim();
        if (StringUtils.hasText(newFullName)) {
            managedTalentUser.setFullName(newFullName);
        }
        managedTalentUser.setPhoneNumber(talentProfileDTO.getPhoneNumber());
        if (talentProfileDTO.getPhotoUrl() != null) { 
            managedTalentUser.setPhotoUrl(talentProfileDTO.getPhotoUrl());
        }

        TalentProfile talentProfile = talentProfileRepository.findByUserId(managedTalentUser.getId())
                .orElseGet(() -> {
                    logger.info("No TalentProfile found for User ID during update: {}. Creating a new one.", managedTalentUser.getId());
                     Assert.notNull(managedTalentUser.getId(), "Managed Talent User ID is null just before TalentProfile constructor call during update.");
                    TalentProfile newProfile = new TalentProfile(managedTalentUser);
                    newProfile.setSubCategories(new ArrayList<>());
                    newProfile.setPortfolioLinks(new ArrayList<>());
                    newProfile.setSkills(new HashSet<>());
                    return newProfile;
                }); 

        talentProfile.setProfileHeadline(talentProfileDTO.getProfileHeadline());
        talentProfile.setBio(talentProfileDTO.getBio());
        talentProfile.setCategory(talentProfileDTO.getCategory());
        talentProfile.setSubCategories(talentProfileDTO.getSubCategories() != null ? new ArrayList<>(talentProfileDTO.getSubCategories()) : new ArrayList<>());
        talentProfile.setYearsOfExperience(talentProfileDTO.getYearsOfExperience());
        talentProfile.setHourlyRate(talentProfileDTO.getHourlyRate());
        talentProfile.setLocation(talentProfileDTO.getLocation());
        talentProfile.setWebsiteLink(talentProfileDTO.getWebsiteLink());
        talentProfile.setPortfolioLinks(talentProfileDTO.getPortfolioLinks() != null ? new ArrayList<>(talentProfileDTO.getPortfolioLinks()) : new ArrayList<>());
        talentProfile.setSkills(talentProfileDTO.getSkills() != null ? new HashSet<>(talentProfileDTO.getSkills()) : new HashSet<>());

        TalentProfile updatedTalentProfile = talentProfileRepository.save(talentProfile);
        logger.info("Updated and saved TalentProfile with ID: {}", updatedTalentProfile.getId());

        return convertToDTO(managedTalentUser, updatedTalentProfile);
    }

    private TalentProfileDTO convertToDTO(User user, TalentProfile talentProfile) {
        String firstName = "";
        String lastName = "";
        if (StringUtils.hasText(user.getFullName())) {
            String[] nameParts = user.getFullName().split("\\s+", 2);
            firstName = nameParts[0];
            if (nameParts.length > 1) {
                lastName = nameParts[1];
            }
        }
        return TalentProfileDTO.builder()
                .id(user.getId())
                .firstName(firstName)
                .lastName(lastName)
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .photoUrl(user.getPhotoUrl())
                .userType(user.getUserType())
                .profileHeadline(talentProfile != null ? talentProfile.getProfileHeadline() : null)
                .bio(talentProfile != null ? talentProfile.getBio() : null)
                .category(talentProfile != null ? talentProfile.getCategory() : null)
                .subCategories(talentProfile != null && talentProfile.getSubCategories() != null ? new ArrayList<>(talentProfile.getSubCategories()) : Collections.emptyList())
                .yearsOfExperience(talentProfile != null ? talentProfile.getYearsOfExperience() : null)
                .hourlyRate(talentProfile != null ? talentProfile.getHourlyRate() : null)
                .location(talentProfile != null ? talentProfile.getLocation() : null)
                .websiteLink(talentProfile != null ? talentProfile.getWebsiteLink() : null)
                .portfolioLinks(talentProfile != null && talentProfile.getPortfolioLinks() != null ? new ArrayList<>(talentProfile.getPortfolioLinks()) : Collections.emptyList())
                .skills(talentProfile != null && talentProfile.getSkills() != null ? new HashSet<>(talentProfile.getSkills()) : Collections.emptySet())
                .build();
    }
} 