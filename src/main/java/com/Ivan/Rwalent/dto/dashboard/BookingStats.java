package com.Ivan.Rwalent.dto.dashboard;

import lombok.Data;

@Data
public class BookingStats {
    private int totalBookings;
    private int pendingBookings;
    private int confirmedBookings;
    private int completedBookings;
    private int canceledBookings;
} 