package com.gogidix.warehousing.marketplace.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Facility Image Entity
 * 
 * Represents images associated with storage facilities.
 */
@Entity
@Table(name = "facility_images")
@Data
@EqualsAndHashCode(callSuper = false)
public class FacilityImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private StorageFacility facility;

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
        EXTERIOR,
        INTERIOR,
        UNIT_SAMPLE,
        AMENITY,
        SECURITY,
        OFFICE,
        ENTRANCE,
        PROMOTIONAL
    }
}