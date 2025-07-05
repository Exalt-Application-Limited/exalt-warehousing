package com.gogidix.warehousing.insurance.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Policy Payment Entity
 * 
 * Represents premium payments made for insurance policies.
 */
@Entity
@Table(name = "policy_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyPayment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String paymentReference;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_policy_id", nullable = false)
    private InsurancePolicy insurancePolicy;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Column(nullable = false)
    private LocalDate billingPeriodStart;
    
    @Column(nullable = false)
    private LocalDate billingPeriodEnd;
    
    @Column(nullable = false)
    private LocalDate dueDate;
    
    private LocalDateTime paidAt;
    
    private LocalDateTime failedAt;
    
    private String failureReason;
    
    private String paymentMethodId;
    
    private String paymentMethodType;
    
    private String transactionId;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (paymentReference == null) {
            paymentReference = generatePaymentReference();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generatePaymentReference() {
        return "PAY-" + System.currentTimeMillis();
    }
    
    public enum PaymentType {
        MONTHLY_PREMIUM, QUARTERLY_PREMIUM, ANNUAL_PREMIUM, SETUP_FEE, LATE_FEE
    }
    
    public enum PaymentStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED, CANCELLED
    }
}