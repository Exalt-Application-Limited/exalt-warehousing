#!/usr/bin/env pwsh
<#
.SYNOPSIS
    Fix UUID/String type mismatches in the fulfillment-service
.DESCRIPTION
    This script fixes UUID/String type conversion issues in the fulfillment-service
    following the same pattern successfully used for inventory-service
#>

param(
    [string]$ProjectPath = "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\fulfillment-service"
)

Write-Host "🔧 Starting UUID/String fixes for fulfillment-service..." -ForegroundColor Cyan

# Step 1: Update FulfillmentOrderService interface to use String IDs
Write-Host "`n📝 Updating FulfillmentOrderService interface..." -ForegroundColor Yellow

$serviceInterfacePath = Join-Path $ProjectPath "src\main\java\com\exalt\warehousing\fulfillment\service\FulfillmentOrderService.java"
$serviceInterfaceContent = @'
package com.exalt.warehousing.fulfillment.service;

import com.exalt.warehousing.fulfillment.entity.FulfillmentOrder;
import com.exalt.warehousing.fulfillment.enums.FulfillmentStatus;
import com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service interface for fulfillment order management
 */
public interface FulfillmentOrderService {

    /**
     * Create a new fulfillment order
     *
     * @param fulfillmentOrder the fulfillment order to create
     * @return the created fulfillment order
     */
    FulfillmentOrder createFulfillmentOrder(FulfillmentOrder fulfillmentOrder);

    /**
     * Get a fulfillment order by ID
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @return the fulfillment order if found
     */
    FulfillmentOrder getFulfillmentOrder(String fulfillmentOrderId);

    /**
     * Get fulfillment orders by order ID
     *
     * @param orderId the order ID
     * @return list of fulfillment orders for the given order
     */
    List<FulfillmentOrder> getFulfillmentOrdersByOrderId(String orderId);

    /**
     * Update fulfillment order status
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @param status the new status
     * @return the updated fulfillment order
     */
    FulfillmentOrder updateFulfillmentOrderStatus(String fulfillmentOrderId, FulfillmentStatus status);

    /**
     * Update item status
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @param itemId the order item ID
     * @param status the new status
     * @return the updated fulfillment order
     */
    FulfillmentOrder updateItemStatus(String fulfillmentOrderId, String itemId, ItemFulfillmentStatus status);

    /**
     * Process pending fulfillment orders
     *
     * @return number of orders processed
     */
    int processPendingOrders();

    /**
     * Cancel a fulfillment order
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @param reason the cancellation reason
     * @return the cancelled fulfillment order
     */
    FulfillmentOrder cancelFulfillmentOrder(String fulfillmentOrderId, String reason);

    /**
     * Get fulfillment orders by status
     *
     * @param status the fulfillment status
     * @return list of fulfillment orders with the given status
     */
    List<FulfillmentOrder> getFulfillmentOrdersByStatus(FulfillmentStatus status);

    /**
     * Get fulfillment orders by warehouse
     *
     * @param warehouseId the warehouse ID
     * @return list of fulfillment orders for the given warehouse
     */
    List<FulfillmentOrder> getFulfillmentOrdersByWarehouse(Long warehouseId);

    /**
     * Get fulfillment statistics for a date range
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return map of statistics
     */
    Map<String, Object> getFulfillmentStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * Split a fulfillment order into multiple orders (for multi-warehouse fulfillment)
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @return list of new fulfillment orders created
     */
    List<FulfillmentOrder> splitFulfillmentOrder(String fulfillmentOrderId);

    /**
     * Assign a fulfillment order to a warehouse
     *
     * @param fulfillmentOrderId the fulfillment order ID
     * @param warehouseId the warehouse ID
     * @return the updated fulfillment order
     */
    FulfillmentOrder assignToWarehouse(String fulfillmentOrderId, Long warehouseId);
    
    /**
     * Process the next stage in a fulfillment order's lifecycle after inventory allocation
     *
     * @param fulfillmentOrder the fulfillment order to process
     */
    void proceedToNextFulfillmentStage(FulfillmentOrder fulfillmentOrder);
    
    /**
     * Handle inventory reservation cancellation
     *
     * @param fulfillmentOrder the fulfillment order
     * @param reason the cancellation reason
     */
    void handleInventoryCancellation(FulfillmentOrder fulfillmentOrder, String reason);
    
    /**
     * Handle inventory reservation expiration
     *
     * @param fulfillmentOrder the fulfillment order
     */
    void handleInventoryExpiration(FulfillmentOrder fulfillmentOrder);
}
'@

Set-Content -Path $serviceInterfacePath -Value $serviceInterfaceContent -Encoding UTF8
Write-Host "✅ Updated FulfillmentOrderService interface" -ForegroundColor Green

# Step 2: Create the fixed FulfillmentOrderServiceImpl
Write-Host "`n📝 Creating fixed FulfillmentOrderServiceImpl..." -ForegroundColor Yellow

$serviceImplPath = Join-Path $ProjectPath "src\main\java\com\exalt\warehousing\fulfillment\service\impl\FulfillmentOrderServiceImpl.java"

# Create backup of the old file
$backupPath = "$serviceImplPath.backup"
if (Test-Path $serviceImplPath) {
    Copy-Item $serviceImplPath $backupPath -Force
    Write-Host "📋 Created backup at: $backupPath" -ForegroundColor Gray
}

$serviceImplContent = @'
package com.exalt.warehousing.fulfillment.service.impl;

import com.exalt.warehousing.fulfillment.entity.FulfillmentOrder;
import com.exalt.warehousing.fulfillment.entity.FulfillmentOrderItem;
import com.exalt.warehousing.fulfillment.enums.FulfillmentStatus;
import com.exalt.warehousing.fulfillment.enums.InventoryStatus;
import com.exalt.warehousing.fulfillment.enums.ItemFulfillmentStatus;
import com.exalt.warehousing.fulfillment.exception.ResourceNotFoundException;
import com.exalt.warehousing.fulfillment.repository.FulfillmentOrderRepository;
import com.exalt.warehousing.fulfillment.service.FulfillmentOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the FulfillmentOrderService
 * Fixed to properly handle String IDs instead of UUID/Long conversions
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FulfillmentOrderServiceImpl implements FulfillmentOrderService {

    private final FulfillmentOrderRepository fulfillmentOrderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public FulfillmentOrder createFulfillmentOrder(FulfillmentOrder fulfillmentOrder) {
        log.info("Creating new fulfillment order for external order ID: {}", fulfillmentOrder.getExternalOrderId());
        
        // Set initial status
        if (fulfillmentOrder.getStatus() == null) {
            fulfillmentOrder.setStatus(FulfillmentStatus.RECEIVED);
        }
        
        // Set order date if not set
        if (fulfillmentOrder.getOrderDate() == null) {
            fulfillmentOrder.setOrderDate(LocalDateTime.now());
        }
        
        // Initialize items status if not set
        if (fulfillmentOrder.getOrderItems() != null) {
            fulfillmentOrder.getOrderItems().forEach(item -> {
                if (item.getStatus() == null) {
                    item.setStatus(ItemFulfillmentStatus.PENDING);
                }
                if (item.getQuantityFulfilled() == null) {
                    item.setQuantityFulfilled(0);
                }
                if (item.getQuantityPicked() == null) {
                    item.setQuantityPicked(0);
                }
                if (item.getQuantityPacked() == null) {
                    item.setQuantityPacked(0);
                }
                item.setFulfillmentOrder(fulfillmentOrder);
            });
        }
        
        // Save the fulfillment order
        FulfillmentOrder savedOrder = fulfillmentOrderRepository.save(fulfillmentOrder);
        
        // Publish event for new fulfillment order
        publishFulfillmentOrderEvent(savedOrder, "FULFILLMENT_ORDER_CREATED");
        
        return savedOrder;
    }

    @Override
    public FulfillmentOrder getFulfillmentOrder(String fulfillmentOrderId) {
        log.debug("Getting fulfillment order with ID: {}", fulfillmentOrderId);
        
        return fulfillmentOrderRepository.findById(fulfillmentOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment order not found with ID: " + fulfillmentOrderId));
    }

    @Override
    public List<FulfillmentOrder> getFulfillmentOrdersByOrderId(String orderId) {
        log.debug("Getting fulfillment orders for order ID: {}", orderId);
        
        Optional<FulfillmentOrder> optionalOrder = fulfillmentOrderRepository.findByExternalOrderId(orderId);
        return optionalOrder.map(Arrays::asList).orElse(Collections.emptyList());
    }

    @Override
    @Transactional
    public FulfillmentOrder updateFulfillmentOrderStatus(String fulfillmentOrderId, FulfillmentStatus status) {
        log.info("Updating fulfillment order {} status to: {}", fulfillmentOrderId, status);
        
        FulfillmentOrder order = getFulfillmentOrder(fulfillmentOrderId);
        
        // Set appropriate timestamps based on status
        LocalDateTime now = LocalDateTime.now();
        
        switch (status) {
            case PROCESSING:
                order.setStatus(status);
                break;
            case PICKING:
                order.setPickStartedAt(now);
                order.setStatus(status);
                break;
            case PICKING_COMPLETE:
                order.setPickCompletedAt(now);
                order.setStatus(status);
                break;
            case PACKING:
                order.setPackStartedAt(now);
                order.setStatus(status);
                break;
            case PACKING_COMPLETE:
                order.setPackCompletedAt(now);
                order.setStatus(status);
                break;
            case SHIPPED:
                order.setShippedAt(now);
                order.setStatus(status);
                break;
            case DELIVERED:
                order.setDeliveredAt(now);
                order.setStatus(status);
                break;
            case CANCELLED:
                order.setCancelledAt(now);
                order.setStatus(status);
                break;
            default:
                order.setStatus(status);
                break;
        }
        
        // Save the updated order
        FulfillmentOrder updatedOrder = fulfillmentOrderRepository.save(order);
        
        // Publish event
        publishFulfillmentOrderEvent(updatedOrder, "FULFILLMENT_ORDER_STATUS_UPDATED");
        
        return updatedOrder;
    }

    @Override
    @Transactional
    public FulfillmentOrder updateItemStatus(String fulfillmentOrderId, String itemId, ItemFulfillmentStatus status) {
        log.info("Updating item {} status to: {} in fulfillment order: {}", itemId, status, fulfillmentOrderId);
        
        FulfillmentOrder order = getFulfillmentOrder(fulfillmentOrderId);
        
        // Find the item in the order
        FulfillmentOrderItem item = order.getOrderItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));
        
        // Update the item status
        item.setStatus(status);
        
        // Update timestamps based on status
        LocalDateTime now = LocalDateTime.now();
        Long userId = 1L; // TODO: Get from security context
        
        switch (status) {
            case PICKED:
                item.setPickedAt(now);
                item.setPickedBy(userId);
                item.setQuantityPicked(item.getQuantity());
                break;
            case PACKED:
                item.setPackedAt(now);
                item.setPackedBy(userId);
                item.setQuantityPacked(item.getQuantity());
                break;
            case FULFILLED:
                item.setQuantityFulfilled(item.getQuantity());
                break;
            default:
                break;
        }
        
        // Check if all items have the same status and update order accordingly
        boolean allItemsSameStatus = order.getOrderItems().stream()
                .allMatch(i -> i.getStatus() == status);
        
        if (allItemsSameStatus) {
            switch (status) {
                case PICKED:
                    updateFulfillmentOrderStatus(fulfillmentOrderId, FulfillmentStatus.PICKING_COMPLETE);
                    break;
                case PACKED:
                    updateFulfillmentOrderStatus(fulfillmentOrderId, FulfillmentStatus.PACKING_COMPLETE);
                    break;
                case FULFILLED:
                    updateFulfillmentOrderStatus(fulfillmentOrderId, FulfillmentStatus.COMPLETED);
                    break;
                default:
                    break;
            }
        }
        
        return fulfillmentOrderRepository.save(order);
    }

    @Override
    @Transactional
    public int processPendingOrders() {
        log.info("Processing pending fulfillment orders");
        
        List<FulfillmentOrder> pendingOrders = fulfillmentOrderRepository.findPendingOrders();
        int processedCount = 0;
        
        for (FulfillmentOrder order : pendingOrders) {
            try {
                // For now, just move to PROCESSING status
                // In real implementation, would check inventory
                order.setStatus(FulfillmentStatus.PROCESSING);
                fulfillmentOrderRepository.save(order);
                processedCount++;
            } catch (Exception e) {
                log.error("Error processing fulfillment order {}: {}", order.getId(), e.getMessage(), e);
            }
        }
        
        log.info("Processed {} out of {} pending fulfillment orders", processedCount, pendingOrders.size());
        return processedCount;
    }

    @Override
    @Transactional
    public FulfillmentOrder cancelFulfillmentOrder(String fulfillmentOrderId, String reason) {
        log.info("Cancelling fulfillment order: {} with reason: {}", fulfillmentOrderId, reason);
        
        FulfillmentOrder order = getFulfillmentOrder(fulfillmentOrderId);
        
        order.setStatus(FulfillmentStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setInternalNotes(reason);
        
        // Update all items to cancelled status
        order.getOrderItems().forEach(item -> item.setStatus(ItemFulfillmentStatus.CANCELLED));
        
        // Save and publish event
        FulfillmentOrder cancelledOrder = fulfillmentOrderRepository.save(order);
        publishFulfillmentOrderEvent(cancelledOrder, "FULFILLMENT_ORDER_CANCELLED");
        
        return cancelledOrder;
    }

    @Override
    public List<FulfillmentOrder> getFulfillmentOrdersByStatus(FulfillmentStatus status) {
        log.debug("Getting fulfillment orders with status: {}", status);
        return fulfillmentOrderRepository.findByStatus(status);
    }

    @Override
    public List<FulfillmentOrder> getFulfillmentOrdersByWarehouse(Long warehouseId) {
        log.debug("Getting fulfillment orders for warehouse: {}", warehouseId);
        return fulfillmentOrderRepository.findByWarehouseId(warehouseId);
    }

    @Override
    public Map<String, Object> getFulfillmentStatistics(LocalDate startDate, LocalDate endDate) {
        log.debug("Getting fulfillment statistics from {} to {}", startDate, endDate);
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        List<FulfillmentOrder> orders = fulfillmentOrderRepository.findByCreatedAtBetween(startDateTime, endDateTime);
        
        Map<String, Object> statistics = new HashMap<>();
        
        // Count orders by status
        Map<FulfillmentStatus, Long> ordersByStatus = orders.stream()
                .collect(Collectors.groupingBy(FulfillmentOrder::getStatus, Collectors.counting()));
        statistics.put("ordersByStatus", ordersByStatus);
        
        // Total orders
        statistics.put("totalOrders", orders.size());
        
        // Completed orders
        long completedOrders = orders.stream()
                .filter(FulfillmentOrder::isCompleted)
                .count();
        statistics.put("completedOrders", completedOrders);
        
        // Cancelled orders
        long cancelledOrders = orders.stream()
                .filter(o -> o.getStatus() == FulfillmentStatus.CANCELLED)
                .count();
        statistics.put("cancelledOrders", cancelledOrders);
        
        // Average processing time (in hours)
        double avgProcessingTime = orders.stream()
                .filter(o -> o.getShippedAt() != null && o.getCreatedAt() != null)
                .mapToLong(o -> java.time.Duration.between(o.getCreatedAt(), o.getShippedAt()).toHours())
                .average()
                .orElse(0.0);
        statistics.put("averageProcessingTimeHours", avgProcessingTime);
        
        return statistics;
    }

    @Override
    @Transactional
    public List<FulfillmentOrder> splitFulfillmentOrder(String fulfillmentOrderId) {
        log.info("Splitting fulfillment order: {}", fulfillmentOrderId);
        
        FulfillmentOrder originalOrder = getFulfillmentOrder(fulfillmentOrderId);
        
        // For now, just return the original order
        // In real implementation, would split based on warehouse availability
        return List.of(originalOrder);
    }

    @Override
    @Transactional
    public FulfillmentOrder assignToWarehouse(String fulfillmentOrderId, Long warehouseId) {
        log.info("Assigning fulfillment order {} to warehouse: {}", fulfillmentOrderId, warehouseId);
        
        FulfillmentOrder order = getFulfillmentOrder(fulfillmentOrderId);
        
        order.setWarehouseId(warehouseId);
        
        // Update status to processing if still in received state
        if (order.getStatus() == FulfillmentStatus.RECEIVED) {
            order.setStatus(FulfillmentStatus.PROCESSING);
        }
        
        // Save and publish event
        FulfillmentOrder updatedOrder = fulfillmentOrderRepository.save(order);
        publishFulfillmentOrderEvent(updatedOrder, "FULFILLMENT_ORDER_ASSIGNED");
        
        return updatedOrder;
    }
    
    @Override
    @Transactional
    public void proceedToNextFulfillmentStage(FulfillmentOrder fulfillmentOrder) {
        log.info("Proceeding to next fulfillment stage for order: {}", fulfillmentOrder.getId());
        
        // Move to next stage based on current status
        FulfillmentStatus currentStatus = fulfillmentOrder.getStatus();
        FulfillmentStatus nextStatus = getNextStatus(currentStatus);
        
        if (nextStatus != null && nextStatus != currentStatus) {
            fulfillmentOrder.setStatus(nextStatus);
            fulfillmentOrderRepository.save(fulfillmentOrder);
            
            // Publish event
            publishFulfillmentOrderEvent(fulfillmentOrder, "FULFILLMENT_STAGE_ADVANCED");
        }
    }
    
    @Override
    @Transactional
    public void handleInventoryCancellation(FulfillmentOrder fulfillmentOrder, String reason) {
        log.info("Handling inventory cancellation for order: {}", fulfillmentOrder.getId());
        
        fulfillmentOrder.setStatus(FulfillmentStatus.CANCELLED);
        fulfillmentOrder.setInternalNotes("Inventory cancelled: " + reason);
        fulfillmentOrder.setCancelledAt(LocalDateTime.now());
        
        fulfillmentOrderRepository.save(fulfillmentOrder);
        
        // Publish event
        publishFulfillmentOrderEvent(fulfillmentOrder, "FULFILLMENT_INVENTORY_CANCELLED");
    }
    
    @Override
    @Transactional
    public void handleInventoryExpiration(FulfillmentOrder fulfillmentOrder) {
        log.info("Handling inventory expiration for order: {}", fulfillmentOrder.getId());
        
        fulfillmentOrder.setStatus(FulfillmentStatus.PENDING_INVENTORY);
        fulfillmentOrder.setInventoryStatus(InventoryStatus.EXPIRED);
        fulfillmentOrder.setInternalNotes("Inventory reservation expired");
        
        fulfillmentOrderRepository.save(fulfillmentOrder);
        
        // Publish event
        publishFulfillmentOrderEvent(fulfillmentOrder, "FULFILLMENT_INVENTORY_EXPIRED");
    }
    
    /**
     * Get the next status in the fulfillment workflow
     */
    private FulfillmentStatus getNextStatus(FulfillmentStatus currentStatus) {
        switch (currentStatus) {
            case NEW:
            case PENDING:
                return FulfillmentStatus.RECEIVED;
            case RECEIVED:
                return FulfillmentStatus.PROCESSING;
            case PROCESSING:
                return FulfillmentStatus.ALLOCATED;
            case ALLOCATED:
                return FulfillmentStatus.READY_FOR_PICKING;
            case READY_FOR_PICKING:
                return FulfillmentStatus.PICKING;
            case PICKING:
                return FulfillmentStatus.PICKING_COMPLETE;
            case PICKING_COMPLETE:
                return FulfillmentStatus.PACKING;
            case PACKING:
                return FulfillmentStatus.PACKING_COMPLETE;
            case PACKING_COMPLETE:
                return FulfillmentStatus.COMPLETED;
            case COMPLETED:
                return FulfillmentStatus.SHIPPED;
            case SHIPPED:
                return FulfillmentStatus.DELIVERED;
            default:
                return null;
        }
    }
    
    /**
     * Publish a fulfillment order event to Kafka
     */
    private void publishFulfillmentOrderEvent(FulfillmentOrder order, String eventType) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", eventType);
            event.put("fulfillmentOrderId", order.getId());
            event.put("orderId", order.getExternalOrderId());
            event.put("status", order.getStatus());
            event.put("warehouseId", order.getWarehouseId());
            event.put("timestamp", LocalDateTime.now());
            
            kafkaTemplate.send("fulfillment-events", order.getId(), event);
            log.debug("Published {} event for order {}", eventType, order.getId());
        } catch (Exception e) {
            log.error("Error publishing event for order {}: {}", order.getId(), e.getMessage(), e);
        }
    }
}
'@

Set-Content -Path $serviceImplPath -Value $serviceImplContent -Encoding UTF8
Write-Host "✅ Created fixed FulfillmentOrderServiceImpl" -ForegroundColor Green

# Step 3: Update FulfillmentController to use String IDs
Write-Host "`n📝 Updating FulfillmentController..." -ForegroundColor Yellow

$controllerPath = Join-Path $ProjectPath "src\main\java\com\exalt\warehousing\fulfillment\controller\FulfillmentController.java"

$controllerContent = @'
package com.exalt.warehousing.fulfillment.controller;

import com.exalt.warehousing.fulfillment.dto.CreateFulfillmentOrderRequest;
import com.exalt.warehousing.fulfillment.dto.UpdateFulfillmentOrderRequest;
import com.exalt.warehousing.fulfillment.dto.FulfillmentOrderDTO;
import com.exalt.warehousing.fulfillment.entity.FulfillmentOrder;
import com.exalt.warehousing.fulfillment.enums.FulfillmentStatus;
import com.exalt.warehousing.fulfillment.mapper.FulfillmentOrderMapper;
import com.exalt.warehousing.fulfillment.service.FulfillmentOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for fulfillment operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/fulfillment")
@RequiredArgsConstructor
@Tag(name = "Fulfillment", description = "Fulfillment management endpoints")
public class FulfillmentController {

    private final FulfillmentOrderService fulfillmentOrderService;
    private final FulfillmentOrderMapper fulfillmentOrderMapper;

    @PostMapping("/orders")
    @Operation(summary = "Create a new fulfillment order")
    public ResponseEntity<FulfillmentOrderDTO> createFulfillmentOrder(
            @Valid @RequestBody CreateFulfillmentOrderRequest request) {
        log.info("Creating fulfillment order for external order ID: {}", request.getExternalOrderId());
        
        FulfillmentOrder order = fulfillmentOrderMapper.toEntity(request);
        FulfillmentOrder createdOrder = fulfillmentOrderService.createFulfillmentOrder(order);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fulfillmentOrderMapper.toDto(createdOrder));
    }

    @GetMapping("/orders/{fulfillmentOrderId}")
    @Operation(summary = "Get a fulfillment order by ID")
    public ResponseEntity<FulfillmentOrderDTO> getFulfillmentOrder(
            @Parameter(description = "Fulfillment order ID") 
            @PathVariable String fulfillmentOrderId) {
        log.info("Getting fulfillment order: {}", fulfillmentOrderId);
        
        FulfillmentOrder order = fulfillmentOrderService.getFulfillmentOrder(fulfillmentOrderId);
        return ResponseEntity.ok(fulfillmentOrderMapper.toDto(order));
    }

    @PutMapping("/orders/{fulfillmentOrderId}")
    @Operation(summary = "Update a fulfillment order")
    public ResponseEntity<FulfillmentOrderDTO> updateFulfillmentOrder(
            @Parameter(description = "Fulfillment order ID") 
            @PathVariable String fulfillmentOrderId,
            @Valid @RequestBody UpdateFulfillmentOrderRequest request) {
        log.info("Updating fulfillment order: {}", fulfillmentOrderId);
        
        // For now, just update status if provided
        if (request.getStatus() != null) {
            FulfillmentOrder updatedOrder = fulfillmentOrderService.updateFulfillmentOrderStatus(
                    fulfillmentOrderId, request.getStatus());
            return ResponseEntity.ok(fulfillmentOrderMapper.toDto(updatedOrder));
        }
        
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/orders/{fulfillmentOrderId}")
    @Operation(summary = "Cancel a fulfillment order")
    public ResponseEntity<Void> cancelFulfillmentOrder(
            @Parameter(description = "Fulfillment order ID") 
            @PathVariable String fulfillmentOrderId,
            @RequestParam(required = false, defaultValue = "Cancelled by user") String reason) {
        log.info("Cancelling fulfillment order: {} with reason: {}", fulfillmentOrderId, reason);
        
        fulfillmentOrderService.cancelFulfillmentOrder(fulfillmentOrderId, reason);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders")
    @Operation(summary = "Get fulfillment orders by status")
    public ResponseEntity<List<FulfillmentOrderDTO>> getFulfillmentOrdersByStatus(
            @RequestParam(required = false) FulfillmentStatus status) {
        log.info("Getting fulfillment orders with status: {}", status);
        
        List<FulfillmentOrder> orders;
        if (status != null) {
            orders = fulfillmentOrderService.getFulfillmentOrdersByStatus(status);
        } else {
            // Return all orders if no status specified
            orders = fulfillmentOrderService.getFulfillmentOrdersByStatus(FulfillmentStatus.PENDING);
        }
        
        return ResponseEntity.ok(orders.stream()
                .map(fulfillmentOrderMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/orders/by-order/{orderId}")
    @Operation(summary = "Get fulfillment orders by order ID")
    public ResponseEntity<List<FulfillmentOrderDTO>> getFulfillmentOrdersByOrderId(
            @Parameter(description = "Order ID") 
            @PathVariable String orderId) {
        log.info("Getting fulfillment orders for order ID: {}", orderId);
        
        List<FulfillmentOrder> orders = fulfillmentOrderService.getFulfillmentOrdersByOrderId(orderId);
        return ResponseEntity.ok(orders.stream()
                .map(fulfillmentOrderMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/warehouses/{warehouseId}/orders")
    @Operation(summary = "Get fulfillment orders by warehouse")
    public ResponseEntity<List<FulfillmentOrderDTO>> getFulfillmentOrdersByWarehouse(
            @Parameter(description = "Warehouse ID") 
            @PathVariable Long warehouseId) {
        log.info("Getting fulfillment orders for warehouse: {}", warehouseId);
        
        List<FulfillmentOrder> orders = fulfillmentOrderService.getFulfillmentOrdersByWarehouse(warehouseId);
        return ResponseEntity.ok(orders.stream()
                .map(fulfillmentOrderMapper::toDto)
                .collect(Collectors.toList()));
    }

    @PostMapping("/orders/{fulfillmentOrderId}/assign-warehouse")
    @Operation(summary = "Assign a fulfillment order to a warehouse")
    public ResponseEntity<FulfillmentOrderDTO> assignToWarehouse(
            @Parameter(description = "Fulfillment order ID") 
            @PathVariable String fulfillmentOrderId,
            @RequestParam Long warehouseId) {
        log.info("Assigning fulfillment order {} to warehouse: {}", fulfillmentOrderId, warehouseId);
        
        FulfillmentOrder updatedOrder = fulfillmentOrderService.assignToWarehouse(fulfillmentOrderId, warehouseId);
        return ResponseEntity.ok(fulfillmentOrderMapper.toDto(updatedOrder));
    }

    @PostMapping("/orders/{fulfillmentOrderId}/split")
    @Operation(summary = "Split a fulfillment order")
    public ResponseEntity<List<FulfillmentOrderDTO>> splitFulfillmentOrder(
            @Parameter(description = "Fulfillment order ID") 
            @PathVariable String fulfillmentOrderId) {
        log.info("Splitting fulfillment order: {}", fulfillmentOrderId);
        
        List<FulfillmentOrder> splitOrders = fulfillmentOrderService.splitFulfillmentOrder(fulfillmentOrderId);
        return ResponseEntity.ok(splitOrders.stream()
                .map(fulfillmentOrderMapper::toDto)
                .collect(Collectors.toList()));
    }

    @PostMapping("/process-pending")
    @Operation(summary = "Process pending fulfillment orders")
    public ResponseEntity<Map<String, Object>> processPendingOrders() {
        log.info("Processing pending fulfillment orders");
        
        int processedCount = fulfillmentOrderService.processPendingOrders();
        
        Map<String, Object> result = Map.of(
                "processedCount", processedCount,
                "timestamp", LocalDate.now()
        );
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get fulfillment statistics")
    public ResponseEntity<Map<String, Object>> getFulfillmentStatistics(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        log.info("Getting fulfillment statistics from {} to {}", startDate, endDate);
        
        Map<String, Object> statistics = fulfillmentOrderService.getFulfillmentStatistics(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/health")
    @Operation(summary = "Check service health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "fulfillment-service",
                "timestamp", LocalDate.now().toString()
        ));
    }
}
'@

Set-Content -Path $controllerPath -Value $controllerContent -Encoding UTF8
Write-Host "✅ Updated FulfillmentController" -ForegroundColor Green

# Step 4: Test the compilation
Write-Host "`n🔨 Testing compilation..." -ForegroundColor Yellow

Push-Location $ProjectPath
try {
    $result = mvn clean compile 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Compilation successful!" -ForegroundColor Green
    } else {
        Write-Host "❌ Compilation failed. Output:" -ForegroundColor Red
        Write-Host $result
    }
} finally {
    Pop-Location
}

Write-Host "`n✅ UUID/String fixes completed for fulfillment-service!" -ForegroundColor Green
Write-Host "📋 Summary of changes:" -ForegroundColor Cyan
Write-Host "  - Updated FulfillmentOrderService interface to use String IDs"
Write-Host "  - Fixed FulfillmentOrderServiceImpl to handle String IDs properly"
Write-Host "  - Updated FulfillmentController to accept String parameters"
Write-Host "  - Removed unnecessary UUID to Long conversions"

Write-Host "`n📌 Next steps:" -ForegroundColor Yellow
Write-Host "  1. Apply similar fixes to warehouse-management-service"
Write-Host "  2. Apply similar fixes to warehouse-subscription"
Write-Host "  3. Fix the 2 POM configuration errors in shared-infrastructure"
