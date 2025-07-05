package com.gogidix.warehousing.insurance.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Insurance Policy Entity
 * 
 * Represents customer storage insurance policies with coverage details.
 */
@Entity
@Table(name = "insurance_policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsurancePolicy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String policyNumber;
    
    @Column(nullable = false)
    private Long customerId;
    
    @Column(nullable = false)
    private String customerName;
    
    @Column(nullable = false)
    private String customerEmail;
    
    @Column(nullable = false)
    private Long storageUnitId;
    
    @Column(nullable = false)
    private String storageUnitNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coverage_plan_id", nullable = false)
    private CoveragePlan coveragePlan;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal coverageAmount;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyPremium;
    
    @Column(nullable = false)
    private BigDecimal deductible;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyStatus status = PolicyStatus.ACTIVE;
    
    @Column(nullable = false)
    private LocalDate effectiveDate;
    
    private LocalDate expirationDate;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime cancelledAt;
    
    private String cancellationReason;
    
    @OneToMany(mappedBy = "insurancePolicy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InsuranceClaim> claims;
    
    @OneToMany(mappedBy = "insurancePolicy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PolicyPayment> payments;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (policyNumber == null) {
            policyNumber = generatePolicyNumber();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generatePolicyNumber() {
        return "INS-" + System.currentTimeMillis();
    }
    
    public enum PolicyStatus {
        ACTIVE, INACTIVE, CANCELLED, EXPIRED, SUSPENDED
    }
}