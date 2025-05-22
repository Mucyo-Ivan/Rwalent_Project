package com.Ivan.Rwalent.controller;

import com.Ivan.Rwalent.dto.ProfileDTO;
import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.service.ProfileService;
import com.Ivan.Rwalent.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth/profile")
public class    ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private final ProfileService profileService;
    private final UserService userService;

    @Autowired
    public ProfileController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ProfileDTO> getProfile() {
        logger.info("Received request to get profile");
        User currentUser = getCurrentUser();
        logger.info("Retrieved current user: {}", currentUser.getEmail());
        ProfileDTO profile = profileService.getProfile(currentUser);
        logger.info("Returning profile for user: {}", currentUser.getEmail());
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<ProfileDTO> updateProfile(@Valid @RequestBody ProfileDTO profileDTO) {
        logger.info("Received request to update profile: {}", profileDTO);
        User currentUser = getCurrentUser();
        logger.info("Updating profile for user: {}", currentUser.getEmail());
        ProfileDTO updatedProfile = profileService.updateProfile(currentUser, profileDTO);
        logger.info("Profile updated successfully for user: {}", currentUser.getEmail());
        return ResponseEntity.ok(updatedProfile);
    }

    @PostMapping("/picture")
    public ResponseEntity<String> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        logger.info("Received request to upload profile picture. File name: {}, Size: {} bytes", 
            file.getOriginalFilename(), file.getSize());
        User currentUser = getCurrentUser();
        logger.info("Uploading picture for user: {}", currentUser.getEmail());
        String pictureUrl = profileService.uploadProfilePicture(currentUser, file);
        logger.info("Picture uploaded successfully for user: {}. URL: {}", currentUser.getEmail(), pictureUrl);
        return ResponseEntity.ok(pictureUrl);
    }

    @GetMapping("/picture")
    public ResponseEntity<Resource> getProfilePicture() {
        logger.info("Received request to get profile picture");
        User currentUser = getCurrentUser();
        logger.info("Retrieving picture for user: {}", currentUser.getEmail());
        Resource picture = profileService.getProfilePicture(currentUser);
        logger.info("Picture retrieved successfully for user: {}", currentUser.getEmail());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(picture);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        logger.info("Getting current user with email: {}", email);
        User user = userService.findUserByEmail(email);
        logger.info("Found user: {}", user != null ? user.getEmail() : "null");
        return user;
    }
} 