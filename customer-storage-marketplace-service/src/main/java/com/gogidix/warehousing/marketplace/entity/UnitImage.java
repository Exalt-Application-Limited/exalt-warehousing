package com.gogidix.warehousing.marketplace.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Unit Image Entity
 * 
 * Represents images associated with specific storage units.
 */
@Entity
@Table(name = "unit_images")
@Data
@EqualsAndHashCode(callSuper = false)
public class UnitImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private StorageUnit unit;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String fileName;

    private String altText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType imageType;

    @Column(nullable = false)
    private Boolean isPrimary = false;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    public enum ImageType {
        UNIT_INTERIOR,
        UNIT_DOOR,
        UNIT_ACCESS,
        UNIT_FEATURES,
        CONDITION_PHOTO
    }
}