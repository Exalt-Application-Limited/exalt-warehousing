package com.gogidix.warehousing.logistics.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.UUID;

/**
 * Feign client for inventory service
 */
@FeignClient(name = "inventory-service", url = "${api.inventory-service.url:}")
public interface InventoryClient {
    
    /**
     * Get inventory item by ID
     *
     * @param id the inventory ID
     * @return the inventory details
     */
    @GetMapping("/inventory/{id}")
    Map<String, Object> getInventoryItem(@PathVariable("id") UUID id);
    
    /**
     * Reserve inventory for transfer
     *
     * @param request the reservation request
     * @return the reservation result
     */
    @PostMapping("/inventory/reserve")
    Map<String, Object> reserveInventory(@RequestBody Map<String, Object> request);
    
    /**
     * Release reserved inventory
     *
     * @param request the release request
     * @return the release result
     */
    @PostMapping("/inventory/release")
    Map<String, Object> releaseInventory(@RequestBody Map<String, Object> request);
    
    /**
     * Transfer inventory between warehouses
     *
     * @param request the transfer request
     * @return the transfer result
     */
    @PostMapping("/inventory/transfer")
    Map<String, Object> transferInventory(@RequestBody Map<String, Object> request);
} 
