package com.Ivan.Rwalent.service;

import com.Ivan.Rwalent.dto.ProfileDTO;
import com.Ivan.Rwalent.model.User;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {
    ProfileDTO getProfile(User user);
    ProfileDTO updateProfile(User user, ProfileDTO profileDTO);
    String uploadProfilePicture(User user, MultipartFile file);
    Resource getProfilePicture(User user);
} 