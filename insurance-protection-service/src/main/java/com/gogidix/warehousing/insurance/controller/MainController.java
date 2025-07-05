package com.gogidix.warehousing.insurance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Main Controller for Storage Insurance and Protection Plans
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Storage Insurance and Protection Plans", description = "Storage Insurance and Protection Plans APIs")
public class MainController {

    @Operation(summary = "Health check", description = "Service health status")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("insurance-protection-service is running");
    }
}
