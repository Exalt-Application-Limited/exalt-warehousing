package com.exalt.warehousing.billing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a line item in an invoice
 */
@Entity
@Table(name = "invoice_line_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @NotBlank
    @Size(max = 255)
    @Column(name = "description", nullable = false)
    private String description;

    @Size(max = 100)
    @Column(name = "item_code")
    private String itemCode;

    @Size(max = 100)
    @Column(name = "item_category")
    private String itemCategory;

    @NotNull
    @PositiveOrZero
    @Column(name = "quantity", precision = 10, scale = 3, nullable = false)
    private BigDecimal quantity;

    @Size(max = 20)
    @Column(name = "unit_of_measure")
    private String unitOfMeasure;

    @NotNull
    @PositiveOrZero
    @Column(name = "unit_price", precision = 15, scale = 4, nullable = false)
    private BigDecimal unitPrice;

    @PositiveOrZero
    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @PositiveOrZero
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @NotNull
    @PositiveOrZero
    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @PositiveOrZero
    @Column(name = "tax_rate", precision = 5, scale = 4)
    private BigDecimal taxRate;

    @PositiveOrZero
    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "period_start")
    private LocalDateTime periodStart;

    @Column(name = "period_end")
    private LocalDateTime periodEnd;

    @Column(name = "subscription_id")
    private UUID subscriptionId;

    @Column(name = "usage_record_id")
    private UUID usageRecordId;

    @Column(name = "warehouse_id")
    private UUID warehouseId;

    @Size(max = 500)
    @Column(name = "notes")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Calculate total amount from quantity and unit price
     */
    public void calculateTotalAmount() {
        BigDecimal lineTotal = quantity.multiply(unitPrice);
        
        // Apply discount
        if (discountAmount != null) {
            lineTotal = lineTotal.subtract(discountAmount);
        } else if (discountPercentage != null) {
            BigDecimal discount = lineTotal.multiply(discountPercentage)
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            lineTotal = lineTotal.subtract(discount);
            this.discountAmount = discount;
        }
        
        // Calculate tax
        if (taxRate != null) {
            this.taxAmount = lineTotal.multiply(taxRate);
        }
        
        this.totalAmount = lineTotal;
    }

    /**
     * Get the line total including tax
     */
    @Transient
    public BigDecimal getTotalWithTax() {
        BigDecimal tax = taxAmount != null ? taxAmount : BigDecimal.ZERO;
        return totalAmount.add(tax);
    }

    /**
     * Check if this line item is for a period-based service
     */
    @Transient
    public boolean isPeriodBased() {
        return periodStart != null && periodEnd != null;
    }

    /**
     * Check if this line item has a discount
     */
    @Transient
    public boolean hasDiscount() {
        return (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) ||
               (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0);
    }

    /**
     * Check if this line item is taxable
     */
    @Transient
    public boolean isTaxable() {
        return taxRate != null && taxRate.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Get effective discount percentage
     */
    @Transient
    public BigDecimal getEffectiveDiscountPercentage() {
        if (discountPercentage != null) {
            return discountPercentage;
        }
        
        if (discountAmount != null && quantity != null && unitPrice != null) {
            BigDecimal lineTotal = quantity.multiply(unitPrice);
            if (lineTotal.compareTo(BigDecimal.ZERO) > 0) {
                return discountAmount.divide(lineTotal, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }
        }
        
        return BigDecimal.ZERO;
    }
}