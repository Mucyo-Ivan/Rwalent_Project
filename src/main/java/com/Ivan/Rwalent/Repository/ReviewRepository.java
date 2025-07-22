package com.Ivan.Rwalent.repository;

import com.Ivan.Rwalent.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    Page<Review> findByReviewedId(Long reviewedId, Pageable pageable);
    
    Page<Review> findByReviewerId(Long reviewerId, Pageable pageable);
    
    Page<Review> findByReviewedIdAndRatingGreaterThanEqual(Long reviewedId, Integer minRating, Pageable pageable);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewed.id = :userId")
    Double findAverageRatingByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewed.id = :userId")
    Long countReviewsByUserId(@Param("userId") Long userId);
    
    // Find reviews made by a specific user about another specific user
    boolean existsByReviewerIdAndReviewedId(Long reviewerId, Long reviewedId);

    @Query("SELECT r FROM Review r WHERE r.reviewed.id = :talentId")
    List<Review> findByTalentId(@Param("talentId") Long talentId);
}
