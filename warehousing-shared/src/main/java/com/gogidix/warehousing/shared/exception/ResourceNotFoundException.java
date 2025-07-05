package com.gogidix.warehousing.shared.exception;

import java.util.Map;
import java.util.UUID;

/**
 * Exception thrown when a requested resource cannot be found
 * 
 * This exception is typically thrown when attempting to retrieve an entity
 * by ID or other unique identifier, but the entity does not exist in the system.
 */
public class ResourceNotFoundException extends WarehouseException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for resource not found by ID
     * 
     * @param resourceType the type of resource (e.g., "Warehouse", "Order")
     * @param resourceId the ID of the resource that was not found
     */
    public ResourceNotFoundException(String resourceType, UUID resourceId) {
        super(
            String.format("%s with ID '%s' not found", resourceType, resourceId),
            "RESOURCE_NOT_FOUND",
            String.format("ResourceType: %s, ID: %s", resourceType, resourceId),
            resourceId,
            null
        );
    }

    /**
     * Constructor for resource not found by string identifier
     * 
     * @param resourceType the type of resource
     * @param identifier the identifier that was not found
     */
    public ResourceNotFoundException(String resourceType, String identifier) {
        super(
            String.format("%s with identifier '%s' not found", resourceType, identifier),
            "RESOURCE_NOT_FOUND",
            String.format("ResourceType: %s, Identifier: %s", resourceType, identifier),
            identifier,
            null
        );
    }

    /**
     * Constructor for resource not found with custom message
     * 
     * @param message custom error message
     */
    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }

    /**
     * Constructor for resource not found with custom message and context
     * 
     * @param message custom error message
     * @param context additional context information
     */
    public ResourceNotFoundException(String message, Map<String, Object> context) {
        super(
            message,
            "RESOURCE_NOT_FOUND",
            context != null ? context.toString() : null,
            context,
            null
        );
    }

    /**
     * Static factory method for warehouse not found
     * 
     * @param warehouseId the warehouse ID
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException warehouseNotFound(UUID warehouseId) {
        return new ResourceNotFoundException("Warehouse", warehouseId);
    }

    /**
     * Static factory method for order not found
     * 
     * @param orderId the order ID
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException orderNotFound(UUID orderId) {
        return new ResourceNotFoundException("Order", orderId);
    }

    /**
     * Static factory method for inventory item not found
     * 
     * @param itemId the inventory item ID
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException inventoryItemNotFound(UUID itemId) {
        return new ResourceNotFoundException("Inventory Item", itemId);
    }

    /**
     * Static factory method for product not found
     * 
     * @param productId the product ID
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException productNotFound(UUID productId) {
        return new ResourceNotFoundException("Product", productId);
    }

    /**
     * Static factory method for shipment not found
     * 
     * @param shipmentId the shipment ID
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException shipmentNotFound(UUID shipmentId) {
        return new ResourceNotFoundException("Shipment", shipmentId);
    }

    /**
     * Static factory method for location not found
     * 
     * @param locationId the location ID
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException locationNotFound(UUID locationId) {
        return new ResourceNotFoundException("Location", locationId);
    }

    /**
     * Static factory method for user not found
     * 
     * @param userId the user ID
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException userNotFound(UUID userId) {
        return new ResourceNotFoundException("User", userId);
    }

    /**
     * Static factory method for tenant not found
     * 
     * @param tenantId the tenant ID
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException tenantNotFound(UUID tenantId) {
        return new ResourceNotFoundException("Tenant", tenantId);
    }

    /**
     * Static factory method for resource not found by SKU
     * 
     * @param sku the SKU
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException productNotFoundBySku(String sku) {
        return new ResourceNotFoundException("Product", "SKU: " + sku);
    }

    /**
     * Static factory method for resource not found by barcode
     * 
     * @param barcode the barcode
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException productNotFoundByBarcode(String barcode) {
        return new ResourceNotFoundException("Product", "Barcode: " + barcode);
    }

    /**
     * Static factory method for configuration not found
     * 
     * @param configKey the configuration key
     * @return ResourceNotFoundException instance
     */
    public static ResourceNotFoundException configurationNotFound(String configKey) {
        return new ResourceNotFoundException("Configuration", "Key: " + configKey);
    }
}