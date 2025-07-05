package com.gogidix.warehousing.logistics.service.impl;

import com.gogidix.warehousing.logistics.client.InventoryClient;
import com.gogidix.warehousing.logistics.client.WarehouseClient;
import com.gogidix.warehousing.logistics.model.TransferItem;
import com.gogidix.warehousing.logistics.model.TransferRequest;
import com.gogidix.warehousing.logistics.service.TransferValidationService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of the TransferValidationService
 */
@Service
@Slf4j
public class TransferValidationServiceImpl implements TransferValidationService {

    private final WarehouseClient warehouseClient;
    private final InventoryClient inventoryClient;

    @Autowired
    public TransferValidationServiceImpl(WarehouseClient warehouseClient, InventoryClient inventoryClient) {
        this.warehouseClient = warehouseClient;
        this.inventoryClient = inventoryClient;
    }

    @Override
    public ValidationResult validateTransferRequest(TransferRequest transferRequest) {
        List<String> errors = new ArrayList<>();
        Map<String, Object> metadata = new HashMap<>();

        // Basic validation
        if (transferRequest.getSourceWarehouseId() == null) {
            errors.add("Source warehouse ID is required");
        }

        if (transferRequest.getDestinationWarehouseId() == null) {
            errors.add("Destination warehouse ID is required");
        }

        if (transferRequest.getSourceWarehouseId() != null && 
                transferRequest.getDestinationWarehouseId() != null &&
                transferRequest.getSourceWarehouseId().equals(transferRequest.getDestinationWarehouseId())) {
            errors.add("Source and destination warehouses must be different");
        }

        if (transferRequest.getItems() == null || transferRequest.getItems().isEmpty()) {
            errors.add("At least one item is required for transfer");
        } else {
            // Validate each item
            for (int i = 0; i < transferRequest.getItems().size(); i++) {
                TransferItem item = transferRequest.getItems().get(i);
                if (item.getInventoryId() == null) {
                    errors.add("Item at index " + i + " is missing inventory ID");
                }
                if (item.getProductId() == null) {
                    errors.add("Item at index " + i + " is missing product ID");
                }
                if (item.getSku() == null || item.getSku().trim().isEmpty()) {
                    errors.add("Item at index " + i + " is missing SKU");
                }
                if (item.getRequestedQuantity() == null || item.getRequestedQuantity() <= 0) {
                    errors.add("Item at index " + i + " has invalid requested quantity");
                }
            }
        }

        // If basic validation passed, perform more advanced validations
        if (errors.isEmpty()) {
            // Validate warehouses
            ValidationResult warehouseValidation = validateWarehouses(transferRequest);
            if (!warehouseValidation.isValid()) {
                errors.addAll(warehouseValidation.getErrors());
                metadata.putAll(warehouseValidation.getMetadata());
            }

            // Validate inventory
            ValidationResult inventoryValidation = validateInventory(transferRequest);
            if (!inventoryValidation.isValid()) {
                errors.addAll(inventoryValidation.getErrors());
                metadata.putAll(inventoryValidation.getMetadata());
            }
        }

        return new ValidationResult(errors.isEmpty(), errors, metadata);
    }

    @Override
    public ValidationResult validateWarehouses(TransferRequest transferRequest) {
        List<String> errors = new ArrayList<>();
        Map<String, Object> metadata = new HashMap<>();

        try {
            // Check source warehouse
            UUID sourceWarehouseId = transferRequest.getSourceWarehouseId();
            boolean sourceExists = warehouseClient.warehouseExists(sourceWarehouseId);
            if (!sourceExists) {
                errors.add("Source warehouse with ID " + sourceWarehouseId + " does not exist");
            } else {
                Map<String, Object> sourceWarehouse = warehouseClient.getWarehouse(sourceWarehouseId);
                metadata.put("sourceWarehouse", sourceWarehouse);
            }

            // Check destination warehouse
            UUID destWarehouseId = transferRequest.getDestinationWarehouseId();
            boolean destExists = warehouseClient.warehouseExists(destWarehouseId);
            if (!destExists) {
                errors.add("Destination warehouse with ID " + destWarehouseId + " does not exist");
            } else {
                Map<String, Object> destWarehouse = warehouseClient.getWarehouse(destWarehouseId);
                metadata.put("destinationWarehouse", destWarehouse);
            }
        } catch (FeignException e) {
            log.error("Error validating warehouses", e);
            errors.add("Error validating warehouses: " + e.getMessage());
        }

        return new ValidationResult(errors.isEmpty(), errors, metadata);
    }

    @Override
    public ValidationResult validateInventory(TransferRequest transferRequest) {
        List<String> errors = new ArrayList<>();
        Map<String, Object> metadata = new HashMap<>();
        Map<String, Map<String, Object>> inventoryItems = new HashMap<>();

        try {
            // Check each inventory item
            for (TransferItem item : transferRequest.getItems()) {
                UUID inventoryId = item.getInventoryId();
                try {
                    Map<String, Object> inventoryItem = inventoryClient.getInventoryItem(inventoryId);
                    inventoryItems.put(inventoryId.toString(), inventoryItem);

                    // Check if inventory belongs to source warehouse
                    UUID warehouseId = UUID.fromString((String) inventoryItem.get("warehouseId"));
                    if (!warehouseId.equals(transferRequest.getSourceWarehouseId())) {
                        errors.add("Inventory item " + inventoryId + " does not belong to source warehouse");
                    }

                    // Check available quantity
                    Integer availableQty = (Integer) inventoryItem.get("availableQuantity");
                    if (availableQty < item.getRequestedQuantity()) {
                        errors.add("Insufficient quantity for inventory item " + inventoryId + 
                                ": requested " + item.getRequestedQuantity() + ", available " + availableQty);
                    }
                } catch (FeignException.NotFound e) {
                    errors.add("Inventory item with ID " + inventoryId + " does not exist");
                }
            }
        } catch (Exception e) {
            log.error("Error validating inventory", e);
            errors.add("Error validating inventory: " + e.getMessage());
        }

        metadata.put("inventoryItems", inventoryItems);
        return new ValidationResult(errors.isEmpty(), errors, metadata);
    }
} 
