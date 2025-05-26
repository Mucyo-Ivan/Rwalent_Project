package com.Ivan.Rwalent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingSummaryDTO {
    private Double averageRating;
    private Long totalReviews;
}
