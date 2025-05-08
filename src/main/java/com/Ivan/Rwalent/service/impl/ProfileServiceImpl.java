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
        profileDTO.setFullName(user.getFullName());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setPhoneNumber(user.getPhoneNumber());
        profileDTO.setLocation(user.getLocation());
        profileDTO.setProfilePictureUrl(user.getPhotoUrl());
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
        if (user.getPhotoUrl() == null) {
            throw new ProfilePictureNotFoundException("Profile picture not found for user: " + user.getEmail());
        }
        return fileStorageUtil.loadFileAsResource(user.getPhotoUrl());
    }
} 