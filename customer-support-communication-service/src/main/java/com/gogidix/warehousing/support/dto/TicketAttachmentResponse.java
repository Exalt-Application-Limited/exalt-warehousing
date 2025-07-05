package com.gogidix.warehousing.support.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Ticket Attachment Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketAttachmentResponse {
    
    private Long id;
    private String fileName;
    private String originalFileName;
    private String contentType;
    private Long fileSize;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
    private String downloadUrl;
}