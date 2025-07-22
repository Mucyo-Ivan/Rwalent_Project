package com.Ivan.Rwalent.dto.dashboard;

import lombok.Data;

@Data
public class RatingStats {
    private int totalRatings;
    private double averageRating;
    private int fiveStarRatings;
    private int fourStarRatings;
    private int threeStarRatings;
    private int twoStarRatings;
    private int oneStarRatings;
} 