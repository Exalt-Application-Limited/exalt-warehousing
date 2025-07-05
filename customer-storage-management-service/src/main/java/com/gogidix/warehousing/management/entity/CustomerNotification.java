package com.gogidix.warehousing.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Customer Notification Entity
 * 
 * Tracks all notifications sent to customers including payment reminders,
 * access information, and system updates.
 */
@Entity
@Table(name = "customer_notifications")
@Data
@EqualsAndHashCode(callSuper = false)
public class CustomerNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_account_id", nullable = false)
    private CustomerAccount customerAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column
    private String templateId;

    @Column
    private String referenceId;

    @Column
    private String referenceType;

    // Delivery Information
    @Column
    private String recipientAddress; // email, phone number, etc.

    @Column
    private LocalDateTime scheduledDate;

    @Column
    private LocalDateTime sentDate;

    @Column
    private LocalDateTime deliveredDate;

    @Column
    private LocalDateTime readDate;

    // Tracking
    @Column
    private String externalMessageId;

    @Column
    private Integer retryCount = 0;

    @Column
    private LocalDateTime lastRetryDate;

    @Column
    private String failureReason;

    // Priority and Expiry
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationPriority priority = NotificationPriority.NORMAL;

    @Column
    private LocalDateTime expiryDate;

    // Metadata
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private String createdBy;

    // Business Methods
    public boolean isDelivered() {
        return this.status == NotificationStatus.DELIVERED ||
               this.status == NotificationStatus.READ;
    }

    public boolean canRetry() {
        return this.status == NotificationStatus.FAILED &&
               this.retryCount < 3;
    }

    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.sentDate = LocalDateTime.now();
    }

    public void markAsDelivered() {
        this.status = NotificationStatus.DELIVERED;
        this.deliveredDate = LocalDateTime.now();
    }

    public void markAsRead() {
        this.status = NotificationStatus.READ;
        this.readDate = LocalDateTime.now();
    }

    public void markAsFailed(String reason) {
        this.status = NotificationStatus.FAILED;
        this.failureReason = reason;
        this.retryCount++;
        this.lastRetryDate = LocalDateTime.now();
    }

    public boolean isExpired() {
        return this.expiryDate != null &&
               LocalDateTime.now().isAfter(this.expiryDate);
    }

    // Enums
    public enum NotificationType {
        PAYMENT_REMINDER,
        PAYMENT_RECEIVED,
        PAYMENT_FAILED,
        LEASE_RENEWAL,
        LEASE_EXPIRING,
        ACCESS_GRANTED,
        ACCESS_REVOKED,
        UNIT_TRANSFER,
        MAINTENANCE_NOTICE,
        PROMOTIONAL,
        SECURITY_ALERT,
        ACCOUNT_UPDATE,
        WELCOME,
        SURVEY
    }

    public enum NotificationChannel {
        EMAIL,
        SMS,
        PUSH_NOTIFICATION,
        IN_APP,
        VOICE_CALL,
        POSTAL_MAIL
    }

    public enum NotificationStatus {
        PENDING,
        SCHEDULED,
        SENT,
        DELIVERED,
        READ,
        FAILED,
        CANCELLED,
        EXPIRED
    }

    public enum NotificationPriority {
        LOW,
        NORMAL,
        HIGH,
        URGENT
    }
}