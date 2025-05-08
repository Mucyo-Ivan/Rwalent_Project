package com.Ivan.Rwalent.controller;

import com.Ivan.Rwalent.dto.ProfileDTO;
import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.security.JwtUtils;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth/profile")
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private final ProfileService profileService;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Autowired
    public ProfileController(ProfileService profileService, UserService userService, JwtUtils jwtUtils) {
        this.profileService = profileService;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    public ResponseEntity<User> getProfile(@RequestHeader("Authorization") String authHeader) {
        logger.info("Received request to get profile");
        try {
            User currentUser = getCurrentUser(authHeader);
            if (currentUser == null) {
                logger.error("No authenticated user found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            logger.info("Returning user profile for: {}", currentUser.getEmail());
            return ResponseEntity.ok(currentUser);
        } catch (Exception e) {
            logger.error("Error processing profile request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    public ResponseEntity<ProfileDTO> updateProfile(@Valid @RequestBody ProfileDTO profileDTO, @RequestHeader("Authorization") String authHeader) {
        logger.info("Received request to update profile: {}", profileDTO);
        User currentUser = getCurrentUser(authHeader);
        if (currentUser == null) {
            logger.error("No authenticated user found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        logger.info("Updating profile for user: {}", currentUser.getEmail());
        ProfileDTO updatedProfile = profileService.updateProfile(currentUser, profileDTO);
        logger.info("Profile updated successfully for user: {}", currentUser.getEmail());
        return ResponseEntity.ok(updatedProfile);
    }

    @PostMapping("/picture")
    public ResponseEntity<String> uploadProfilePicture(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String authHeader) {
        logger.info("Received request to upload profile picture. File name: {}, Size: {} bytes",
                file.getOriginalFilename(), file.getSize());
        User currentUser = getCurrentUser(authHeader);
        if (currentUser == null) {
            logger.error("No authenticated user found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        logger.info("Uploading picture for user: {}", currentUser.getEmail());
        String pictureUrl = profileService.uploadProfilePicture(currentUser, file);
        logger.info("Picture uploaded successfully for user: {}. URL: {}", currentUser.getEmail(), pictureUrl);
        return ResponseEntity.ok(pictureUrl);
    }

    @GetMapping("/picture")
    public ResponseEntity<Resource> getProfilePicture(@RequestHeader("Authorization") String authHeader) {
        logger.info("Received request to get profile picture");
        User currentUser = getCurrentUser(authHeader);
        if (currentUser == null) {
            logger.error("No authenticated user found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        logger.info("Retrieving picture for user: {}", currentUser.getEmail());
        Resource picture = profileService.getProfilePicture(currentUser);
        logger.info("Picture retrieved successfully for user: {}", currentUser.getEmail());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(picture);
    }

    private User getCurrentUser(String authHeader) {
        logger.info("Getting current user from Authorization header");
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.error("Invalid or missing Authorization header");
                return null;
            }

            String jwt = authHeader.substring(7); // Remove "Bearer " prefix
            logger.info("Extracted JWT token");

            if (!jwtUtils.validateJwtToken(jwt)) {
                logger.error("JWT token is invalid");
                return null;
            }

            String email = jwtUtils.extractUsername(jwt);
            logger.info("Extracted email from JWT: {}", email);

            if (email == null) {
                logger.error("No email found in JWT token");
                return null;
            }

            User user = userService.findUserByEmail(email);
            logger.info("Found user: {}", user != null ? user.getEmail() : "null");
            return user;

        } catch (Exception e) {
            logger.error("Unexpected error in getCurrentUser: {}", e.getMessage());
            return null;
        }
    }
}