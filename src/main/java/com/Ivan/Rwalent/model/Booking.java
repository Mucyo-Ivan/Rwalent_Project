package com.Ivan.Rwalent.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "talent_id", nullable = false)
    private User talent;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime bookingDate;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(length = 500) // Optional: Increase length if notes can be long
    private String notes;

    // --- New Fields --- 
    @Column(nullable = true) // Location might be optional depending on service type
    private String eventLocation;

    @Column(precision = 10, scale = 2, nullable = true) // Price might be set later or optional
    private BigDecimal agreedPrice;

    @Column(length = 1000) // Optional: Increase length for detailed requirements
    private String eventRequirements;
    // --- End New Fields ---

    public enum BookingStatus {
        PENDING,
        CONFIRMED,
        COMPLETED,
        CANCELED
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getTalent() {
        return talent;
    }

    public void setTalent(User talent) {
        this.talent = talent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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