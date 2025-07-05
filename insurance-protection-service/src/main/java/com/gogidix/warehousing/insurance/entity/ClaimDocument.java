package com.gogidix.warehousing.insurance.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Claim Document Entity
 * 
 * Represents documents and evidence submitted with insurance claims.
 */
@Entity
@Table(name = "claim_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimDocument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_claim_id", nullable = false)
    private InsuranceClaim insuranceClaim;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private String originalFileName;
    
    @Column(nullable = false)
    private String contentType;
    
    @Column(nullable = false)
    private Long fileSize;
    
    @Column(nullable = false)
    private String filePath;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String uploadedBy;
    
    @Column(nullable = false)
    private LocalDateTime uploadedAt;
    
    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
    
    public enum DocumentType {
        INCIDENT_PHOTO, DAMAGE_ASSESSMENT, POLICE_REPORT, RECEIPT, INVOICE, 
        REPAIR_ESTIMATE, PROOF_OF_OWNERSHIP, WITNESS_STATEMENT, OTHER
    }
}