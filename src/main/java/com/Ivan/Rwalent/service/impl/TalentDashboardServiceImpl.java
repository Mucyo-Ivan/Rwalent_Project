package com.Ivan.Rwalent.service.impl;

import com.Ivan.Rwalent.dto.dashboard.*;
import com.Ivan.Rwalent.model.Booking;
import com.Ivan.Rwalent.model.Review;
import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.repository.BookingRepository;
import com.Ivan.Rwalent.repository.ReviewRepository;
import com.Ivan.Rwalent.repository.UserRepository;
import com.Ivan.Rwalent.service.TalentDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TalentDashboardServiceImpl implements TalentDashboardService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public TalentDashboardServiceImpl(UserRepository userRepository,
                                    BookingRepository bookingRepository,
                                    ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dashboardData", key = "#talentId")
    public TalentDashboardData getDashboardData(Long talentId) {
        TalentDashboardData dashboardData = new TalentDashboardData();
        dashboardData.setEarnings(getEarnings(talentId, "all"));
        dashboardData.setProfileStats(getProfileStats(talentId));
        dashboardData.setBookingStats(getBookingStats(talentId));
        dashboardData.setMonthlyBookings(getMonthlyBookings(talentId, LocalDate.now().getYear()));
        dashboardData.setLocationDistribution(getLocationDistribution(talentId));
        dashboardData.setRatingStats(getRatingStats(talentId));
        dashboardData.setServiceDistribution(getServiceDistribution(talentId));
        return dashboardData;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "monthlyBookings", key = "#talentId + '-' + #year")
    public List<MonthlyBookingData> getMonthlyBookings(Long talentId, int year) {
        List<Booking> bookings = bookingRepository.findByTalentId(talentId);
        Map<YearMonth, List<Booking>> bookingsByMonth = bookings.stream()
                .collect(Collectors.groupingBy(booking -> YearMonth.from(booking.getBookingDate())));

        List<MonthlyBookingData> monthlyData = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();
        
        for (int i = 0; i < 12; i++) {
            YearMonth month = currentMonth.minusMonths(i);
            List<Booking> monthBookings = bookingsByMonth.getOrDefault(month, Collections.emptyList());

            MonthlyBookingData data = new MonthlyBookingData();
            data.setMonth(month);
            data.setTotalBookings(monthBookings.size());
            data.setTotalEarnings(monthBookings.stream()
                    .map(Booking::getAgreedPrice)
                    .filter(Objects::nonNull)
                    .mapToDouble(BigDecimal::doubleValue)
                    .sum());
            data.setCompletedBookings((int) monthBookings.stream()
                    .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED)
                    .count());
            data.setCanceledBookings((int) monthBookings.stream()
                    .filter(b -> b.getStatus() == Booking.BookingStatus.CANCELED)
                    .count());

            monthlyData.add(data);
        }

        return monthlyData;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "earnings", key = "#talentId + '-' + #period")
    public EarningsData getEarnings(Long talentId, String period) {
        List<Booking> bookings = bookingRepository.findByTalentId(talentId);
        LocalDate now = LocalDate.now();
        
        EarningsData earnings = new EarningsData();
        earnings.setTotal(bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED)
                .map(Booking::getAgreedPrice)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .sum());

        earnings.setThisMonth(bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED)
                .filter(b -> YearMonth.from(b.getBookingDate()).equals(YearMonth.from(now)))
                .map(Booking::getAgreedPrice)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .sum());

        earnings.setLastMonth(bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED)
                .filter(b -> YearMonth.from(b.getBookingDate()).equals(YearMonth.from(now.minusMonths(1))))
                .map(Booking::getAgreedPrice)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .sum());

        earnings.setThisYear(bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED)
                .filter(b -> b.getBookingDate().getYear() == now.getYear())
                .map(Booking::getAgreedPrice)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .sum());

        return earnings;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "profileStats", key = "#talentId")
    public ProfileStats getProfileStats(Long talentId) {
        User talent = userRepository.findById(talentId)
                .orElseThrow(() -> new RuntimeException("Talent not found"));

        ProfileStats stats = new ProfileStats();
        stats.setProfileViews(talent.getProfileViews());
        
        // Calculate profile completion percentage
        int totalFields = 7; // Adjust based on your profile fields
        int completedFields = 0;
        
        if (talent.getFullName() != null && !talent.getFullName().isEmpty()) completedFields++;
        if (talent.getEmail() != null && !talent.getEmail().isEmpty()) completedFields++;
        if (talent.getPhoneNumber() != null && !talent.getPhoneNumber().isEmpty()) completedFields++;
        if (talent.getLocation() != null && !talent.getLocation().isEmpty()) completedFields++;
        if (talent.getBio() != null && !talent.getBio().isEmpty()) completedFields++;
        if (talent.getServiceAndPricing() != null && !talent.getServiceAndPricing().isEmpty()) completedFields++;
        if (talent.getProfilePicture() != null && !talent.getProfilePicture().isEmpty()) completedFields++;

        stats.setProfileCompletion((int) ((double) completedFields / totalFields * 100));
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "bookingStats", key = "#talentId")
    public BookingStats getBookingStats(Long talentId) {
        List<Booking> bookings = bookingRepository.findByTalentId(talentId);
        
        BookingStats stats = new BookingStats();
        stats.setTotalBookings(bookings.size());
        stats.setPendingBookings((int) bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.PENDING)
                .count());
        stats.setConfirmedBookings((int) bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED)
                .count());
        stats.setCompletedBookings((int) bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED)
                .count());
        stats.setCanceledBookings((int) bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CANCELED)
                .count());
        
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "serviceDistribution", key = "#talentId")
    public List<ServiceDistribution> getServiceDistribution(Long talentId) {
        List<Booking> bookings = bookingRepository.findByTalentId(talentId);
        Map<String, List<Booking>> bookingsByService = bookings.stream()
                .collect(Collectors.groupingBy(Booking::getEventRequirements));

        int totalBookings = bookings.size();

        return bookingsByService.entrySet().stream()
                .map(entry -> {
                    ServiceDistribution dist = new ServiceDistribution();
                    dist.setServiceName(entry.getKey());
                    dist.setBookingCount(entry.getValue().size());
                    dist.setPercentage((double) entry.getValue().size() / totalBookings * 100);
                    dist.setTotalEarnings(entry.getValue().stream()
                            .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED)
                            .map(Booking::getAgreedPrice)
                            .filter(Objects::nonNull)
                            .mapToDouble(BigDecimal::doubleValue)
                            .sum());
                    return dist;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "locationDistribution", key = "#talentId")
    public List<LocationDistribution> getLocationDistribution(Long talentId) {
        List<Booking> bookings = bookingRepository.findByTalentId(talentId);
        Map<String, List<Booking>> bookingsByLocation = bookings.stream()
                .collect(Collectors.groupingBy(Booking::getEventLocation));

        int totalBookings = bookings.size();

        return bookingsByLocation.entrySet().stream()
                .map(entry -> {
                    LocationDistribution dist = new LocationDistribution();
                    dist.setLocation(entry.getKey());
                    dist.setBookingCount(entry.getValue().size());
                    dist.setPercentage((double) entry.getValue().size() / totalBookings * 100);
                    return dist;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "ratingStats", key = "#talentId")
    public RatingStats getRatingStats(Long talentId) {
        List<Review> reviews = reviewRepository.findByTalentId(talentId);
        RatingStats stats = new RatingStats();
        
        if (reviews.isEmpty()) {
            return stats;
        }

        stats.setTotalRatings(reviews.size());
        stats.setAverageRating(reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0));

        Map<Integer, Long> ratingCounts = reviews.stream()
                .collect(Collectors.groupingBy(
                        Review::getRating,
                        Collectors.counting()
                ));

        stats.setFiveStarRatings(ratingCounts.getOrDefault(5, 0L).intValue());
        stats.setFourStarRatings(ratingCounts.getOrDefault(4, 0L).intValue());
        stats.setThreeStarRatings(ratingCounts.getOrDefault(3, 0L).intValue());
        stats.setTwoStarRatings(ratingCounts.getOrDefault(2, 0L).intValue());
        stats.setOneStarRatings(ratingCounts.getOrDefault(1, 0L).intValue());

        return stats;
    }
} 