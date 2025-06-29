package com.exalt.warehousing.logistics.controller;

import com.exalt.warehousing.logistics.integration.TransferWorkflowCoordinator;
import com.exalt.warehousing.logistics.model.TransferRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for transfer workflow operations
 */
@RestController
@RequestMapping("/transfers/workflow")
@Slf4j
@Tag(name = "Transfer Workflow", description = "API for transfer workflow operations")
public class TransferWorkflowController {

    private final TransferWorkflowCoordinator workflowCoordinator;

    @Autowired
    public TransferWorkflowController(TransferWorkflowCoordinator workflowCoordinator) {
        this.workflowCoordinator = workflowCoordinator;
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve a transfer request")
    public ResponseEntity<TransferRequest> approveTransfer(
            @Parameter(description = "Transfer request ID") @PathVariable("id") UUID transferRequestId) {
        log.info("REST request to approve transfer request: {}", transferRequestId);
        TransferRequest result = workflowCoordinator.approveTransfer(transferRequestId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/complete-picking")
    @Operation(summary = "Complete picking for a transfer request")
    public ResponseEntity<TransferRequest> completePickingForTransfer(
            @Parameter(description = "Transfer request ID") @PathVariable("id") UUID transferRequestId) {
        log.info("REST request to complete picking for transfer request: {}", transferRequestId);
        TransferRequest result = workflowCoordinator.completePickingForTransfer(transferRequestId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/complete-packing")
    @Operation(summary = "Complete packing for a transfer request")
    public ResponseEntity<TransferRequest> completePackingForTransfer(
            @Parameter(description = "Transfer request ID") @PathVariable("id") UUID transferRequestId) {
        log.info("REST request to complete packing for transfer request: {}", transferRequestId);
        TransferRequest result = workflowCoordinator.completePackingForTransfer(transferRequestId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/pickup")
    @Operation(summary = "Record pickup for a transfer request")
    public ResponseEntity<TransferRequest> pickupTransfer(
            @Parameter(description = "Transfer request ID") @PathVariable("id") UUID transferRequestId,
            @Parameter(description = "Carrier ID") @RequestParam String carrierId,
            @Parameter(description = "Tracking number") @RequestParam String trackingNumber,
            @Parameter(description = "Shipping label URL") @RequestParam(required = false) String shippingLabelUrl) {
        log.info("REST request to record pickup for transfer request: {}", transferRequestId);
        TransferRequest result = workflowCoordinator.pickupTransfer(transferRequestId, carrierId, trackingNumber, shippingLabelUrl);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/arrive")
    @Operation(summary = "Record arrival for a transfer request")
    public ResponseEntity<TransferRequest> arriveTransfer(
            @Parameter(description = "Transfer request ID") @PathVariable("id") UUID transferRequestId) {
        log.info("REST request to record arrival for transfer request: {}", transferRequestId);
        TransferRequest result = workflowCoordinator.arriveTransfer(transferRequestId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/verify")
    @Operation(summary = "Verify a transfer request")
    public ResponseEntity<TransferRequest> verifyTransfer(
            @Parameter(description = "Transfer request ID") @PathVariable("id") UUID transferRequestId) {
        log.info("REST request to verify transfer request: {}", transferRequestId);
        TransferRequest result = workflowCoordinator.verifyTransfer(transferRequestId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete a transfer request")
    public ResponseEntity<TransferRequest> completeTransfer(
            @Parameter(description = "Transfer request ID") @PathVariable("id") UUID transferRequestId) {
        log.info("REST request to complete transfer request: {}", transferRequestId);
        TransferRequest result = workflowCoordinator.completeTransfer(transferRequestId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a transfer request")
    public ResponseEntity<TransferRequest> cancelTransfer(
            @Parameter(description = "Transfer request ID") @PathVariable("id") UUID transferRequestId) {
        log.info("REST request to cancel transfer request: {}", transferRequestId);
        TransferRequest result = workflowCoordinator.cancelTransfer(transferRequestId);
        return ResponseEntity.ok(result);
    }
} 
