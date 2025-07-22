package com.Ivan.Rwalent.dto.dashboard;

import java.time.YearMonth;
import java.util.List;

public class MonthlyBookingData {
    private YearMonth month;
    private int totalBookings;
    private double totalEarnings;
    private int completedBookings;
    private int canceledBookings;

    public MonthlyBookingData() {
        // Default constructor
    }

    public MonthlyBookingData(YearMonth month, int totalBookings, double totalEarnings, int completedBookings, int canceledBookings) {
        this.month = month;
        this.totalBookings = totalBookings;
        this.totalEarnings = totalEarnings;
        this.completedBookings = completedBookings;
        this.canceledBookings = canceledBookings;
    }

    public YearMonth getMonth() {
        return month;
    }

    public void setMonth(YearMonth month) {
        this.month = month;
    }

    public int getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(int totalBookings) {
        this.totalBookings = totalBookings;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public int getCompletedBookings() {
        return completedBookings;
    }

    public void setCompletedBookings(int completedBookings) {
        this.completedBookings = completedBookings;
    }

    public int getCanceledBookings() {
        return canceledBookings;
    }

    public void setCanceledBookings(int canceledBookings) {
        this.canceledBookings = canceledBookings;
    }
} 