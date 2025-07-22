package com.Ivan.Rwalent.dto.dashboard;

import lombok.Data;

@Data
public class ServiceDistribution {
    private String serviceName;
    private int bookingCount;
    private double percentage;
    private double totalEarnings;
} 