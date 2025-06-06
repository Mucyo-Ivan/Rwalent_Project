package com.Ivan.Rwalent.controller;

import com.Ivan.Rwalent.dto.TalentSearchDTO;
import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.service.UserService;
import com.Ivan.Rwalent.service.ProfileService;
import com.Ivan.Rwalent.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/talents") // Base path for talent-related endpoints
public class TalentController {

    private final UserService userService;
    private final ProfileService profileService;

    @Autowired
    public TalentController(UserService userService, ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    // Moved from UserController
    @PostMapping("/search")
    public ResponseEntity<Page<User>> searchTalents(@RequestBody TalentSearchDTO searchDTO) {
        Page<User> talents = userService.searchTalents(searchDTO);
        return new ResponseEntity<>(talents, HttpStatus.OK);
    }

    // Moved from UserController
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<User>> getTalentsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        TalentSearchDTO searchDTO = new TalentSearchDTO();
        // Ensure category conversion handles potential errors gracefully
        try {
            searchDTO.setCategory(User.TalentCategory.fromValue(category.toUpperCase()));
        } catch (IllegalArgumentException e) {
            // Optionally return BAD_REQUEST or search without category
            return ResponseEntity.badRequest().build(); // Example: Return bad request for invalid category
        }

        searchDTO.setPage(page);
        searchDTO.setSize(size);
        searchDTO.setSortBy(sortBy);
        searchDTO.setSortDirection(sortDirection);

        Page<User> talents = userService.searchTalents(searchDTO);
        return new ResponseEntity<>(talents, HttpStatus.OK);
    }

    // Get talent by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getTalentById(@PathVariable Long id) {
        User talent = userService.getUserById(id);
        if (talent == null) {
            throw new ResourceNotFoundException("Talent not found with id: " + id);
        }

        // Verify the user is a talent
        if (talent.getUserType() != User.UserType.TALENT) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(talent);
    }

    // Get talent profile picture by ID
    @GetMapping("/{id}/picture")
    public ResponseEntity<Resource> getTalentProfilePicture(@PathVariable Long id) {
        User talent = userService.getUserById(id);
        if (talent == null) {
            throw new ResourceNotFoundException("Talent not found with id: " + id);
        }

        // Verify the user is a talent
        if (talent.getUserType() != User.UserType.TALENT) {
            return ResponseEntity.notFound().build();
        }

        try {
            Resource picture = profileService.getProfilePicture(talent);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(picture);
        } catch (Exception e) {
            // Return a default image or 404
            return ResponseEntity.notFound().build();
        }
    }
}