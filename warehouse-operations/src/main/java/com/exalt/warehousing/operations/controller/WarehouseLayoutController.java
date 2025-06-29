package com.exalt.warehousing.operations.controller;

import com.exalt.warehousing.operations.entity.WarehouseLayout;
import com.exalt.warehousing.operations.service.WarehouseLayoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST Controller for WarehouseLayout operations
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/warehouse-operations/layouts")
@RequiredArgsConstructor
@CrossOrigin
public class WarehouseLayoutController {

    private final WarehouseLayoutService warehouseLayoutService;

    @PostMapping
    public ResponseEntity<WarehouseLayout> createLayout(@Valid @RequestBody WarehouseLayout layout) {
        WarehouseLayout created = warehouseLayoutService.createLayout(layout);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseLayout> getLayout(@PathVariable Long id) {
        return warehouseLayoutService.findById(id)
                .map(layout -> ResponseEntity.ok(layout))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<WarehouseLayout>> getAllLayouts() {
        List<WarehouseLayout> layouts = warehouseLayoutService.findAll();
        return ResponseEntity.ok(layouts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseLayout> updateLayout(@PathVariable Long id, @Valid @RequestBody WarehouseLayout layout) {
        WarehouseLayout updated = warehouseLayoutService.updateLayout(id, layout);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLayout(@PathVariable Long id) {
        warehouseLayoutService.deleteLayout(id);
        return ResponseEntity.noContent().build();
    }
}

