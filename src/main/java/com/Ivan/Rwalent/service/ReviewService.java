package com.Ivan.Rwalent.service;

import com.Ivan.Rwalent.dto.CreateReviewRequest;
import com.Ivan.Rwalent.dto.RatingSummaryDTO;
import com.Ivan.Rwalent.dto.ReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    
    /**
     * Creates a new review
     * 
     * @param reviewerId ID of the user creating the review
     * @param request review details
     * @return the created review
     */
    ReviewDTO createReview(Long reviewerId, CreateReviewRequest request);
    
    /**
     * Gets all reviews for a specific user
     * 
     * @param userId ID of the user to get reviews for
     * @param pageable pagination information
     * @return page of reviews
     */
    Page<ReviewDTO> getReviewsForUser(Long userId, Pageable pageable);
    
    /**
     * Gets all reviews created by a specific user
     * 
     * @param userId ID of the reviewer
     * @param pageable pagination information
     * @return page of reviews
     */
    Page<ReviewDTO> getReviewsByUser(Long userId, Pageable pageable);
    
    /**
     * Gets all reviews for a user with a minimum rating
     * 
     * @param userId ID of the user to get reviews for
     * @param minRating minimum rating value (1-5)
     * @param pageable pagination information
     * @return page of filtered reviews
     */
    Page<ReviewDTO> getReviewsForUserWithMinRating(Long userId, Integer minRating, Pageable pageable);
    
    /**
     * Gets rating summary for a user
     * 
     * @param userId ID of the user to get rating summary for
     * @return rating summary with average rating and total review count
     */
    RatingSummaryDTO getRatingSummary(Long userId);
    
    /**
     * Deletes a review
     * 
     * @param reviewId ID of the review to delete
     * @param currentUserId ID of the user trying to delete the review
     * @param isAdmin whether the current user is an admin
     * @return true if deleted successfully
     */
    boolean deleteReview(Long reviewId, Long currentUserId, boolean isAdmin);
    
    /**
     * Checks if a user has already reviewed another user
     * 
     * @param reviewerId ID of the reviewer
     * @param reviewedId ID of the user being reviewed
     * @return true if a review already exists
     */
    boolean hasUserReviewedTalent(Long reviewerId, Long reviewedId);
}
