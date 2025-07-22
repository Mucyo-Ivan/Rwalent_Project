package com.Ivan.Rwalent.dto.dashboard;

import java.util.List;
import com.Ivan.Rwalent.dto.dashboard.*;

public class TalentDashboardData {
    private EarningsData earnings;
    private ProfileStats profileStats;
    private BookingStats bookingStats;
    private List<MonthlyBookingData> monthlyBookings;
    private List<LocationDistribution> locationDistribution;
    private RatingStats ratingStats;
    private List<ServiceDistribution> serviceDistribution;

    // Getters and Setters
    public EarningsData getEarnings() {
        return earnings;
    }

    public void setEarnings(EarningsData earnings) {
        this.earnings = earnings;
    }

    public ProfileStats getProfileStats() {
        return profileStats;
    }

    public void setProfileStats(ProfileStats profileStats) {
        this.profileStats = profileStats;
    }

    public BookingStats getBookingStats() {
        return bookingStats;
    }

    public void setBookingStats(BookingStats bookingStats) {
        this.bookingStats = bookingStats;
    }

    public List<MonthlyBookingData> getMonthlyBookings() {
        return monthlyBookings;
    }

    public void setMonthlyBookings(List<MonthlyBookingData> monthlyBookings) {
        this.monthlyBookings = monthlyBookings;
    }

    public List<LocationDistribution> getLocationDistribution() {
        return locationDistribution;
    }

    public void setLocationDistribution(List<LocationDistribution> locationDistribution) {
        this.locationDistribution = locationDistribution;
    }

    public RatingStats getRatingStats() {
        return ratingStats;
    }

    public void setRatingStats(RatingStats ratingStats) {
        this.ratingStats = ratingStats;
    }

    public List<ServiceDistribution> getServiceDistribution() {
        return serviceDistribution;
    }

    public void setServiceDistribution(List<ServiceDistribution> serviceDistribution) {
        this.serviceDistribution = serviceDistribution;
    }
} 