package com.Ivan.Rwalent.controller;

import com.Ivan.Rwalent.dto.dashboard.*;
import com.Ivan.Rwalent.service.TalentDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/talent")
public class TalentDashboardController {

    private final TalentDashboardService dashboardService;

    @Autowired
    public TalentDashboardController(TalentDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<TalentDashboardData> getDashboardData(@RequestParam(required = false, defaultValue = "1") Long talentId) {
        TalentDashboardData data = dashboardService.getDashboardData(talentId);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/bookings/monthly")
    public ResponseEntity<List<MonthlyBookingData>> getMonthlyBookings(
            @RequestParam(required = false, defaultValue = "1") Long talentId,
            @RequestParam(required = false, defaultValue = "2024") int year) {
        List<MonthlyBookingData> data = dashboardService.getMonthlyBookings(talentId, year);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/earnings")
    public ResponseEntity<EarningsData> getEarnings(
            @RequestParam(required = false, defaultValue = "1") Long talentId,
            @RequestParam(required = false, defaultValue = "all") String period) {
        EarningsData data = dashboardService.getEarnings(talentId, period);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/profile/stats")
    public ResponseEntity<ProfileStats> getProfileStats(@RequestParam(required = false, defaultValue = "1") Long talentId) {
        ProfileStats stats = dashboardService.getProfileStats(talentId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/bookings/stats")
    public ResponseEntity<BookingStats> getBookingStats(@RequestParam(required = false, defaultValue = "1") Long talentId) {
        BookingStats stats = dashboardService.getBookingStats(talentId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/bookings/service-distribution")
    public ResponseEntity<List<ServiceDistribution>> getServiceDistribution(@RequestParam(required = false, defaultValue = "1") Long talentId) {
        List<ServiceDistribution> distribution = dashboardService.getServiceDistribution(talentId);
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/bookings/location-distribution")
    public ResponseEntity<List<LocationDistribution>> getLocationDistribution(@RequestParam(required = false, defaultValue = "1") Long talentId) {
        List<LocationDistribution> distribution = dashboardService.getLocationDistribution(talentId);
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/ratings/stats")
    public ResponseEntity<RatingStats> getRatingStats(@RequestParam(required = false, defaultValue = "1") Long talentId) {
        RatingStats stats = dashboardService.getRatingStats(talentId);
        return ResponseEntity.ok(stats);
    }
} 