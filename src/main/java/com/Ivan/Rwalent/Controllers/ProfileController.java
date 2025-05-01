package com.Ivan.Rwalent.Controllers;

import com.Ivan.Rwalent.DTO.ProfileDTO;
import com.Ivan.Rwalent.model.Profile;
import com.Ivan.Rwalent.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @PostMapping
    public ResponseEntity<Profile> createOrUpdateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProfileDTO profileDTO) {
        Long userId = Long.valueOf(userDetails.getUsername());
        Profile profile = profileService.createOrUpdateProfile(userId, profileDTO);
        return ResponseEntity.ok(profile);
    }

    @GetMapping
    public ResponseEntity<Profile> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());
        Profile profile = profileService.getProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());
        profileService.deleteProfile(userId);
        return ResponseEntity.noContent().build();
    }
}
