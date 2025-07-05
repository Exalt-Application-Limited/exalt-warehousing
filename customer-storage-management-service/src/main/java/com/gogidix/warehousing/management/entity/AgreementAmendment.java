package com.gogidix.warehousing.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Agreement Amendment Entity
 * 
 * Tracks changes and amendments to rental agreements.
 */
@Entity
@Table(name = "agreement_amendments")
@Data
@EqualsAndHashCode(callSuper = false)
public class AgreementAmendment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_agreement_id", nullable = false)
    private RentalAgreement rentalAgreement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AmendmentType amendmentType;

    @Column(nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String details;

    // Change Values
    @Column
    private String previousValue;

    @Column
    private String newValue;

    @Column
    private BigDecimal previousAmount;

    @Column
    private BigDecimal newAmount;

    // Approval
    @Column(nullable = false)
    private Boolean customerApproved = false;

    @Column
    private LocalDateTime approvalDate;

    @Column
    private String approvedBy;

    // Effective Date
    @Column(nullable = false)
    private LocalDateTime effectiveDate;

    // Metadata
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String createdBy;

    @Column
    private String reason;

    // Enums
    public enum AmendmentType {
        RATE_CHANGE,
        UNIT_TRANSFER,
        TERM_EXTENSION,
        TERM_REDUCTION,
        INSURANCE_CHANGE,
        PAYMENT_METHOD_CHANGE,
        ACCESS_MODIFICATION,
        CONTACT_UPDATE,
        SPECIAL_TERMS,
        FEE_WAIVER,
        DISCOUNT_APPLIED,
        OTHER
    }
}