package com.Ivan.Rwalent.controller;

import com.Ivan.Rwalent.dto.CreateReviewRequest;
import com.Ivan.Rwalent.dto.RatingSummaryDTO;
import com.Ivan.Rwalent.dto.ReviewDTO;
import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Create a new review
     * 
     * @param request The review details
     * @param authentication Current authenticated user
     * @return The created review
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            Authentication authentication) {
        
        User currentUser = (User) authentication.getPrincipal();
        ReviewDTO createdReview = reviewService.createReview(currentUser.getId(), request);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    /**
     * Get all reviews for a specific user
     * 
     * @param userId ID of the user to get reviews for
     * @param page Page number (0-based)
     * @param size Page size
     * @param sortBy Field to sort by
     * @param direction Sort direction (ASC or DESC)
     * @param minRating Optional minimum rating filter
     * @return Page of reviews
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Page<ReviewDTO>> getReviewsForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) Integer minRating) {
        
        Pageable pageable = PageRequest.of(
            page, 
            size, 
            Sort.Direction.fromString(direction), 
            sortBy
        );
        
        Page<ReviewDTO> reviews;
        if (minRating != null) {
            reviews = reviewService.getReviewsForUserWithMinRating(userId, minRating, pageable);
        } else {
            reviews = reviewService.getReviewsForUser(userId, pageable);
        }
        
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get all reviews created by the current authenticated user
     * 
     * @param authentication Current authenticated user
     * @param page Page number (0-based)
     * @param size Page size
     * @param sortBy Field to sort by
     * @param direction Sort direction (ASC or DESC)
     * @return Page of reviews
     */
    @GetMapping("/mine")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ReviewDTO>> getMyReviews(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        User currentUser = (User) authentication.getPrincipal();
        
        Pageable pageable = PageRequest.of(
            page, 
            size, 
            Sort.Direction.fromString(direction), 
            sortBy
        );
        
        Page<ReviewDTO> reviews = reviewService.getReviewsByUser(currentUser.getId(), pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get rating summary for a user
     * 
     * @param userId ID of the user to get rating summary for
     * @return Rating summary with average rating and total review count
     */
    @GetMapping("/{userId}/rating-summary")
    public ResponseEntity<RatingSummaryDTO> getRatingSummary(@PathVariable Long userId) {
        RatingSummaryDTO ratingSummary = reviewService.getRatingSummary(userId);
        return ResponseEntity.ok(ratingSummary);
    }

    /**
     * Check if the current user has already reviewed a specific talent
     * 
     * @param talentId ID of the talent
     * @param authentication Current authenticated user
     * @return Boolean indicating if a review exists
     */
    @GetMapping("/check/{talentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> hasReviewedTalent(
            @PathVariable Long talentId,
            Authentication authentication) {
        
        User currentUser = (User) authentication.getPrincipal();
        boolean hasReviewed = reviewService.hasUserReviewedTalent(currentUser.getId(), talentId);
        
        return ResponseEntity.ok(hasReviewed);
    }

    /**
     * Delete a review
     * 
     * @param reviewId ID of the review to delete
     * @param authentication Current authenticated user
     * @return No content if deleted successfully
     */
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            Authentication authentication) {
        
        User currentUser = (User) authentication.getPrincipal();
        boolean isAdmin = authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        
        reviewService.deleteReview(reviewId, currentUser.getId(), isAdmin);
        
        return ResponseEntity.noContent().build();
    }
}
