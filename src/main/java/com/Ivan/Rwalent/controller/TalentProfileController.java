package com.Ivan.Rwalent.controller;

import com.Ivan.Rwalent.dto.TalentProfileDTO;
import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.service.TalentProfileService;
import com.Ivan.Rwalent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/talent/profile")
public class TalentProfileController {

    private final TalentProfileService talentProfileService;
    private final UserService userService; // To fetch the current User object

    @Autowired
    public TalentProfileController(TalentProfileService talentProfileService, UserService userService) {
        this.talentProfileService = talentProfileService;
        this.userService = userService;
    }

    private User getCurrentAuthenticatedUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            // This case should ideally be caught by Spring Security earlier
            throw new UsernameNotFoundException("User not authenticated");
        }
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        if (user.getUserType() != User.UserType.TALENT) {
            // Although PreAuthorize should handle this, an extra check here can be useful.
            throw new org.springframework.security.access.AccessDeniedException("Access is denied. User is not a talent.");
        }
        return user;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') and @userService.getUserByEmail(principal.username).userType.name() == 'TALENT'")
    public ResponseEntity<TalentProfileDTO> getMyTalentProfile(Authentication authentication) {
        User talentUser = getCurrentAuthenticatedUser(authentication);
        TalentProfileDTO profileDTO = talentProfileService.getTalentProfile(talentUser);
        return ResponseEntity.ok(profileDTO);
    }

    @PutMapping
    @PreAuthorize("hasRole('USER') and @userService.getUserByEmail(principal.username).userType.name() == 'TALENT'")
    public ResponseEntity<TalentProfileDTO> updateMyTalentProfile(Authentication authentication, @RequestBody TalentProfileDTO talentProfileDTO) {
        User talentUser = getCurrentAuthenticatedUser(authentication);
        TalentProfileDTO updatedProfileDTO = talentProfileService.updateTalentProfile(talentUser, talentProfileDTO);
        return ResponseEntity.ok(updatedProfileDTO);
    }
} 