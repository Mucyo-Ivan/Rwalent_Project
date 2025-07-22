package com.Ivan.Rwalent.dto.dashboard;

import lombok.Data;

@Data
public class LocationDistribution {
    private String location;
    private int bookingCount;
    private double percentage;
} 