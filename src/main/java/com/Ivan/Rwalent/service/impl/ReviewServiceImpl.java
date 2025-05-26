package com.Ivan.Rwalent.service.impl;

import com.Ivan.Rwalent.dto.CreateReviewRequest;
import com.Ivan.Rwalent.dto.RatingSummaryDTO;
import com.Ivan.Rwalent.dto.ReviewDTO;
import com.Ivan.Rwalent.exception.ResourceNotFoundException;
import com.Ivan.Rwalent.model.Review;
import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.repository.ReviewRepository;
import com.Ivan.Rwalent.repository.UserRepository;
import com.Ivan.Rwalent.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewDTO createReview(Long reviewerId, CreateReviewRequest request) {
        // Get the reviewer
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found with id: " + reviewerId));
                
        // Get the user being reviewed
        User reviewed = userRepository.findById(request.getReviewedId())
                .orElseThrow(() -> new ResourceNotFoundException("User to be reviewed not found with id: " + request.getReviewedId()));
        
        // Check if reviewer is not reviewing themselves
        if (reviewerId.equals(request.getReviewedId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot review yourself");
        }
        
        // Enforce that only regular users can review talents (one-way rating)
        if (reviewer.getUserType() != User.UserType.REGULAR || reviewed.getUserType() != User.UserType.TALENT) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "Only regular users can submit reviews for talents"
            );
        }
        
        // Check if the reviewer has already reviewed this user
        if (reviewRepository.existsByReviewerIdAndReviewedId(reviewerId, request.getReviewedId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already reviewed this talent");
        }
        
        // Create and save the review
        Review review = Review.builder()
                .reviewer(reviewer)
                .reviewed(reviewed)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
                
        Review savedReview = reviewRepository.save(review);
        
        return ReviewDTO.fromEntity(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsForUser(Long userId, Pageable pageable) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        
        return reviewRepository.findByReviewedId(userId, pageable)
                .map(ReviewDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByUser(Long userId, Pageable pageable) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        
        return reviewRepository.findByReviewerId(userId, pageable)
                .map(ReviewDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsForUserWithMinRating(Long userId, Integer minRating, Pageable pageable) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        
        // Validate rating
        if (minRating < 1 || minRating > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
        }
        
        return reviewRepository.findByReviewedIdAndRatingGreaterThanEqual(userId, minRating, pageable)
                .map(ReviewDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public RatingSummaryDTO getRatingSummary(Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        
        Double averageRating = reviewRepository.findAverageRatingByUserId(userId);
        Long totalReviews = reviewRepository.countReviewsByUserId(userId);
        
        // If no reviews yet, set average to 0.0
        if (averageRating == null) {
            averageRating = 0.0;
        }
        
        return RatingSummaryDTO.builder()
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .build();
    }

    @Override
    @Transactional
    public boolean deleteReview(Long reviewId, Long currentUserId, boolean isAdmin) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        // Check if the current user is the reviewer or an admin
        if (!review.getReviewer().getId().equals(currentUserId) && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this review");
        }
        
        reviewRepository.delete(review);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewedTalent(Long reviewerId, Long reviewedId) {
        return reviewRepository.existsByReviewerIdAndReviewedId(reviewerId, reviewedId);
    }
}
