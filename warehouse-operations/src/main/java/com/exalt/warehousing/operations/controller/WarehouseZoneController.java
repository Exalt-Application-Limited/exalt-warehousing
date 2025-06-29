package com.exalt.warehousing.operations.controller;

import com.exalt.warehousing.operations.entity.WarehouseZone;
import com.exalt.warehousing.operations.service.WarehouseZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST Controller for WarehouseZone operations
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/warehouse-operations/zones")
@RequiredArgsConstructor
@CrossOrigin
public class WarehouseZoneController {

    private final WarehouseZoneService warehouseZoneService;

    @PostMapping
    public ResponseEntity<WarehouseZone> createZone(@Valid @RequestBody WarehouseZone zone) {
        WarehouseZone created = warehouseZoneService.createZone(zone);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseZone> getZone(@PathVariable Long id) {
        return warehouseZoneService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/layout/{layoutId}")
    public ResponseEntity<List<WarehouseZone>> getZonesByLayout(@PathVariable Long layoutId) {
        List<WarehouseZone> zones = warehouseZoneService.findByLayoutId(layoutId);
        return ResponseEntity.ok(zones);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseZone> updateZone(@PathVariable Long id, @Valid @RequestBody WarehouseZone zone) {
        WarehouseZone updated = warehouseZoneService.updateZone(id, zone);
        return ResponseEntity.ok(updated);
    }
}

