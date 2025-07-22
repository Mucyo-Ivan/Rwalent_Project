package com.Ivan.Rwalent.service;

import com.Ivan.Rwalent.dto.dashboard.*;
import com.Ivan.Rwalent.dto.dashboard.MonthlyBookingData;
import java.util.List;

public interface TalentDashboardService {
    TalentDashboardData getDashboardData(Long talentId);
    List<MonthlyBookingData> getMonthlyBookings(Long talentId, int year);
    EarningsData getEarnings(Long talentId, String period);
    ProfileStats getProfileStats(Long talentId);
    BookingStats getBookingStats(Long talentId);
    List<ServiceDistribution> getServiceDistribution(Long talentId);
    List<LocationDistribution> getLocationDistribution(Long talentId);
    RatingStats getRatingStats(Long talentId);
} 