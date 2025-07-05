package com.gogidix.warehousing.marketplace.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Unit Reservation Entity
 * 
 * Represents a customer's reservation of a storage unit.
 * Tracks booking details, payment information, and reservation status.
 */
@Entity
@Table(name = "unit_reservations")
@Data
@EqualsAndHashCode(callSuper = false)
public class UnitReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reservationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private StorageUnit unit;

    // Customer Information
    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String customerPhone;

    // Reservation Details
    @Column(nullable = false)
    private LocalDateTime reservationDate;

    @Column(nullable = false)
    private LocalDateTime moveInDate;

    @Column
    private LocalDateTime moveOutDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationType reservationType;

    @Column(nullable = false)
    private Integer durationMonths;

    // Pricing Information
    @Column(nullable = false)
    private BigDecimal monthlyRate;

    @Column(nullable = false)
    private BigDecimal securityDeposit;

    private BigDecimal adminFee;

    private BigDecimal insuranceFee;

    @Column(nullable = false)
    private BigDecimal totalUpfrontCost;

    @Column(nullable = false)
    private BigDecimal totalContractValue;

    // Payment Information
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column
    private String paymentTransactionId;

    @Column
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    // Reservation Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column
    private LocalDateTime confirmationDate;

    @Column
    private LocalDateTime cancellationDate;

    @Column
    private String cancellationReason;

    // Access and Security
    @Column
    private String accessCode;

    @Column
    private String lockCombination;

    @Column(nullable = false)
    private Boolean hasReceivedAccessInstructions = false;

    // Special Requirements and Notes
    @Column(columnDefinition = "TEXT")
    private String specialRequirements;

    @Column(columnDefinition = "TEXT")
    private String customerNotes;

    @Column(columnDefinition = "TEXT")
    private String adminNotes;

    // Metadata
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime lastModifiedBy;

    // Business methods
    public boolean isConfirmed() {
        return this.status == ReservationStatus.CONFIRMED;
    }

    public boolean isPaid() {
        return this.paymentStatus == PaymentStatus.PAID;
    }

    public boolean canBeCancelled() {
        return this.status == ReservationStatus.PENDING || 
               this.status == ReservationStatus.CONFIRMED;
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
        this.confirmationDate = LocalDateTime.now();
    }

    public void cancel(String reason) {
        this.status = ReservationStatus.CANCELLED;
        this.cancellationDate = LocalDateTime.now();
        this.cancellationReason = reason;
        this.isActive = false;
    }

    public void markAsPaid(String transactionId, PaymentMethod method) {
        this.paymentStatus = PaymentStatus.PAID;
        this.paymentTransactionId = transactionId;
        this.paymentMethod = method;
        this.paymentDate = LocalDateTime.now();
    }

    public void activate() {
        this.status = ReservationStatus.ACTIVE;
        this.isActive = true;
    }

    public void complete() {
        this.status = ReservationStatus.COMPLETED;
        this.isActive = false;
    }

    public boolean isExpiringSoon() {
        if (this.moveOutDate == null) return false;
        LocalDateTime thirtyDaysFromNow = LocalDateTime.now().plusDays(30);
        return this.moveOutDate.isBefore(thirtyDaysFromNow);
    }

    public boolean isOverdue() {
        if (this.moveOutDate == null) return false;
        return this.moveOutDate.isBefore(LocalDateTime.now());
    }

    public BigDecimal getRefundableAmount() {
        if (this.paymentStatus != PaymentStatus.PAID) {
            return BigDecimal.ZERO;
        }
        
        // Calculate refund based on cancellation policy
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(this.moveInDate.minusDays(7))) {
            // Full refund if cancelled 7+ days before move-in
            return this.totalUpfrontCost.subtract(this.adminFee != null ? this.adminFee : BigDecimal.ZERO);
        } else if (now.isBefore(this.moveInDate)) {
            // Partial refund if cancelled within 7 days
            return this.securityDeposit;
        } else {
            // No refund after move-in date
            return BigDecimal.ZERO;
        }
    }

    // Enums
    public enum ReservationType {
        SHORT_TERM,     // 1-6 months
        LONG_TERM,      // 6+ months
        MONTH_TO_MONTH, // Ongoing monthly
        SEASONAL,       // Specific seasons
        TEMPORARY       // Under 1 month
    }

    public enum ReservationStatus {
        PENDING,        // Initial reservation, awaiting payment
        CONFIRMED,      // Payment received, reservation confirmed
        ACTIVE,         // Customer has moved in
        COMPLETED,      // Customer has moved out
        CANCELLED,      // Reservation cancelled
        EXPIRED,        // Reservation expired without payment
        ON_HOLD         // Temporarily on hold
    }

    public enum PaymentStatus {
        PENDING,        // Payment not yet made
        PAID,           // Payment completed
        FAILED,         // Payment failed
        REFUNDED,       // Payment refunded
        PARTIAL_REFUND  // Partial refund issued
    }

    public enum PaymentMethod {
        CREDIT_CARD,
        DEBIT_CARD,
        BANK_TRANSFER,
        DIGITAL_WALLET,
        CASH,
        CHECK,
        AUTO_PAY
    }
}