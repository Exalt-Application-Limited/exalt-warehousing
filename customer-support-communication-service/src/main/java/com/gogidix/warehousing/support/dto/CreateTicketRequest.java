package com.gogidix.warehousing.support.dto;

import com.gogidix.warehousing.support.entity.SupportTicket;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Create Support Ticket Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketRequest {
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @NotBlank(message = "Subject is required")
    private String subject;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Category is required")
    private SupportTicket.TicketCategory category;
    
    private SupportTicket.TicketPriority priority = SupportTicket.TicketPriority.MEDIUM;
}