package com.Ivan.Rwalent.service.impl;

import com.Ivan.Rwalent.dto.ProfileDTO;
import com.Ivan.Rwalent.exception.FileStorageException;
import com.Ivan.Rwalent.exception.ProfilePictureNotFoundException;
import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.service.ProfileService;
import com.Ivan.Rwalent.service.UserService;
import com.Ivan.Rwalent.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserService userService;
    private final FileStorageUtil fileStorageUtil;

    @Autowired
    public ProfileServiceImpl(UserService userService, FileStorageUtil fileStorageUtil) {
        this.userService = userService;
        this.fileStorageUtil = fileStorageUtil;
    }

    @Override
    public ProfileDTO getProfile(User user) {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(user.getId());
        profileDTO.setFullName(user.getFullName());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setPhoneNumber(user.getPhoneNumber());
        profileDTO.setLocation(user.getLocation());
        profileDTO.setProfilePictureUrl(user.getPhotoUrl());
        profileDTO.setUserType(user.getUserType());
        return profileDTO;
    }

    @Override
    public ProfileDTO updateProfile(User user, ProfileDTO profileDTO) {
        user.setFullName(profileDTO.getFullName());
        user.setEmail(profileDTO.getEmail());
        user.setPhoneNumber(profileDTO.getPhoneNumber());
        user.setLocation(profileDTO.getLocation());
        
        User updatedUser = userService.updateUser(user);
        return getProfile(updatedUser);
    }

    @Override
    public String uploadProfilePicture(User user, MultipartFile file) {
        try {
            String fileName = fileStorageUtil.storeFile(file);
            
            // Store the actual filename in the database (not the URL)
            user.setProfilePicture(fileName);
            
            // We'll also keep the URL path for backward compatibility
            String fileUrl = "/api/auth/profile/picture";
            user.setPhotoUrl(fileUrl);
            
            userService.updateUser(user);
            
            return fileUrl;
        } catch (Exception e) {
            throw new FileStorageException("Could not store file " + file.getOriginalFilename() + ". Please try again!", e);
        }
    }

    @Override
    public Resource getProfilePicture(User user) {
        // First try to get the profile picture using the filename stored in profilePicture field
        if (user.getProfilePicture() != null) {
            try {
                return fileStorageUtil.loadFileAsResource(user.getProfilePicture());
            } catch (Exception e) {
                // If there's an error, we'll fall back to the old method or throw an exception
            }
        }
        
        // Fallback: check if photoUrl exists but is not a URL path (legacy data)
        if (user.getPhotoUrl() != null && !user.getPhotoUrl().startsWith("/")) {
            try {
                return fileStorageUtil.loadFileAsResource(user.getPhotoUrl());
            } catch (Exception e) {
                // Continue to the exception below
            }
        }
        
        throw new ProfilePictureNotFoundException("Profile picture not found for user: " + user.getEmail());
    }
}