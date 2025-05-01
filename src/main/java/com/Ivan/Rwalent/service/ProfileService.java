package com.Ivan.Rwalent.service;

import com.Ivan.Rwalent.DTO.ProfileDTO;
import com.Ivan.Rwalent.model.Profile;
import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.Repository.ProfileRepository;
import com.Ivan.Rwalent.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Profile createOrUpdateProfile(Long userId, ProfileDTO profileDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile profile = profileRepository.findByUserId(userId)
                .orElse(new Profile());

        profile.setUser(user);
        profile.setFullName(profileDTO.getFullName());
        profile.setBio(profileDTO.getBio());
        profile.setSkills(profileDTO.getSkills());
        profile.setLocation(profileDTO.getLocation());
        profile.setProfilePictureUrl(profileDTO.getProfilePictureUrl());

        return profileRepository.save(profile);
    }

    public Profile getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    @Transactional
    public void deleteProfile(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        profileRepository.delete(profile);
    }
} 