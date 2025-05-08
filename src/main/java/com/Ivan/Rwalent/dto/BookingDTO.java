package com.Ivan.Rwalent.dto;

import com.Ivan.Rwalent.model.Booking.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookingDTO {
    private Long id;
    private Long talentId;
    private Long userId;
    private LocalDateTime bookingDate;
    private Integer durationMinutes;
    private BookingStatus status;
    private String notes;
    
    // --- New Fields ---
    private String eventLocation;
    private BigDecimal agreedPrice;
    private String eventRequirements;
    // --- End New Fields ---

    private String talentName;
    private String userName;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTalentId() {
        return talentId;
    }

    public void setTalentId(Long talentId) {
        this.talentId = talentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTalentName() {
        return talentName;
    }

    public void setTalentName(String talentName) {
        this.talentName = talentName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // --- Getters and Setters for New Fields ---
    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public BigDecimal getAgreedPrice() {
        return agreedPrice;
    }

    public void setAgreedPrice(BigDecimal agreedPrice) {
        this.agreedPrice = agreedPrice;
    }

    public String getEventRequirements() {
        return eventRequirements;
    }

    public void setEventRequirements(String eventRequirements) {
        this.eventRequirements = eventRequirements;
    }
    // --- End Getters and Setters for New Fields ---
} 