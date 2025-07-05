package com.gogidix.warehousing.logistics.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.UUID;

/**
 * Feign client for warehouse service
 */
@FeignClient(name = "warehouse-service", url = "${api.warehouse-service.url:}")
public interface WarehouseClient {
    
    /**
     * Get warehouse details by ID
     *
     * @param id the warehouse ID
     * @return the warehouse details
     */
    @GetMapping("/warehouses/{id}")
    Map<String, Object> getWarehouse(@PathVariable("id") UUID id);
    
    /**
     * Validate warehouse exists
     *
     * @param id the warehouse ID
     * @return true if exists
     */
    @GetMapping("/warehouses/{id}/exists")
    boolean warehouseExists(@PathVariable("id") UUID id);
} 
