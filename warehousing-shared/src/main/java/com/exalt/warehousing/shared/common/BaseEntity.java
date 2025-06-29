package com.exalt.warehousing.shared.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base Entity
 * 
 * Common base class for all entities with shared audit fields and ID generation.
 * This class provides common functionality for all entities in the system.
 * 
 * Key Features:
 * - UUID-based ID generation
 * - Creation and modification tracking
 * - Audit trail with timestamps
 * - User tracking for modifications
 * 
 * @author Warehousing Development Team
 * @version 1.0.0
 * @since 2023-12-01
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key using UUID format
     */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private String id;

    /**
     * Creation timestamp
     */
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Last modification timestamp
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * User who created the entity
     */
    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 50)
    private String createdBy;

    /**
     * User who last modified the entity
     */
    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    /**
     * Soft deletion flag
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    /**
     * Deletion timestamp
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * User who deleted the entity
     */
    @Column(name = "deleted_by", length = 50)
    private String deletedBy;

    /**
     * Version for optimistic locking
     */
    @Version
    @Column(name = "version")
    private Integer version;

    /**
     * Pre-persist hook to set default values before entity creation
     */
    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = createdAt;
        }
        if (deleted == null) {
            deleted = false;
        }
        if (version == null) {
            version = 0;
        }
    }

    /**
     * Pre-update hook to update timestamps on modifications
     */
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Check if the entity is newly created (not yet persisted)
     * 
     * @return true if entity is new, false if already persisted
     */
    @JsonIgnore
    @Transient
    public boolean isNew() {
        return id == null || createdAt == null;
    }

    /**
     * Soft delete the entity
     * 
     * @param deletedBy Username of user performing deletion
     */
    public void softDelete(String deletedBy) {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    /**
     * Restore a soft-deleted entity
     */
    public void restore() {
        this.deleted = false;
        this.deletedAt = null;
        this.deletedBy = null;
    }

    /**
     * Check if entity is active (not deleted)
     * 
     * @return true if entity is active, false if deleted
     */
    @JsonIgnore
    @Transient
    public boolean isActive() {
        return !deleted;
    }
}