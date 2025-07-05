package com.gogidix.warehousing.logistics.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Main Controller for Moving and Delivery Coordination
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Moving and Delivery Coordination", description = "Moving and Delivery Coordination APIs")
public class MainController {

    @Operation(summary = "Health check", description = "Service health status")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("moving-logistics-service is running");
    }
}
