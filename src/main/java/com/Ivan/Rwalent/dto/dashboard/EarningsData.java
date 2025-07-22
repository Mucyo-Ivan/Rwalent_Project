package com.Ivan.Rwalent.dto.dashboard;

public class EarningsData {
    private double total;
    private double thisMonth;
    private double lastMonth;
    private double thisYear;

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getThisMonth() {
        return thisMonth;
    }

    public void setThisMonth(double thisMonth) {
        this.thisMonth = thisMonth;
    }

    public double getLastMonth() {
        return lastMonth;
    }

    public void setLastMonth(double lastMonth) {
        this.lastMonth = lastMonth;
    }

    public double getThisYear() {
        return thisYear;
    }

    public void setThisYear(double thisYear) {
        this.thisYear = thisYear;
    }
} 