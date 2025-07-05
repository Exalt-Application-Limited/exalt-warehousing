package com.gogidix.warehousing.logistics.controller;

import com.gogidix.warehousing.logistics.model.TransferItemStatus;
import com.gogidix.warehousing.logistics.model.TransferRequest;
import com.gogidix.warehousing.logistics.model.TransferStatus;
import com.gogidix.warehousing.logistics.service.TransferService;
import com.gogidix.warehousing.shared.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for transfer request operations
 */
@RestController
@RequestMapping("/transfers")
@Slf4j
@Tag(name = "Transfer Management", description = "APIs for managing transfers between warehouses")
public class TransferController {

    private final TransferService transferService;

    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @Operation(summary = "Create a new transfer request")
    public ResponseEntity<TransferRequest> createTransferRequest(
            @Valid @RequestBody TransferRequest transferRequest) {
        log.info("REST request to create transfer request");
        TransferRequest createdRequest = transferService.createTransferRequest(transferRequest);
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a transfer request by ID")
    public ResponseEntity<TransferRequest> getTransferRequest(
            @Parameter(description = "Transfer request ID") @PathVariable UUID id) {
        log.info("REST request to get transfer request with id: {}", id);
        return transferService.getTransferRequest(id)
                .map(transferRequest -> new ResponseEntity<>(transferRequest, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Transfer request not found with id: " + id));
    }

    @GetMapping("/reference/{referenceNumber}")
    @Operation(summary = "Get a transfer request by reference number")
    public ResponseEntity<TransferRequest> getTransferRequestByReferenceNumber(
            @Parameter(description = "Reference number") @PathVariable String referenceNumber) {
        log.info("REST request to get transfer request with reference number: {}", referenceNumber);
        return transferService.getTransferRequestByReferenceNumber(referenceNumber)
                .map(transferRequest -> new ResponseEntity<>(transferRequest, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Transfer request not found with reference number: " + referenceNumber));
    }

    @GetMapping
    @Operation(summary = "Get all transfer requests")
    public ResponseEntity<List<TransferRequest>> getAllTransferRequests() {
        log.info("REST request to get all transfer requests");
        List<TransferRequest> transferRequests = transferService.getAllTransferRequests();
        return new ResponseEntity<>(transferRequests, HttpStatus.OK);
    }

    @GetMapping("/source-warehouse/{warehouseId}")
    @Operation(summary = "Get transfer requests by source warehouse ID")
    public ResponseEntity<List<TransferRequest>> getTransferRequestsBySourceWarehouse(
            @Parameter(description = "Source warehouse ID") @PathVariable UUID warehouseId) {
        log.info("REST request to get transfer requests from source warehouse: {}", warehouseId);
        List<TransferRequest> transferRequests = transferService.getTransferRequestsBySourceWarehouse(warehouseId);
        return new ResponseEntity<>(transferRequests, HttpStatus.OK);
    }

    @GetMapping("/destination-warehouse/{warehouseId}")
    @Operation(summary = "Get transfer requests by destination warehouse ID")
    public ResponseEntity<List<TransferRequest>> getTransferRequestsByDestinationWarehouse(
            @Parameter(description = "Destination warehouse ID") @PathVariable UUID warehouseId) {
        log.info("REST request to get transfer requests to destination warehouse: {}", warehouseId);
        List<TransferRequest> transferRequests = transferService.getTransferRequestsByDestinationWarehouse(warehouseId);
        return new ResponseEntity<>(transferRequests, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get transfer requests by status")
    public ResponseEntity<List<TransferRequest>> getTransferRequestsByStatus(
            @Parameter(description = "Transfer status") @PathVariable TransferStatus status) {
        log.info("REST request to get transfer requests with status: {}", status);
        List<TransferRequest> transferRequests = transferService.getTransferRequestsByStatus(status);
        return new ResponseEntity<>(transferRequests, HttpStatus.OK);
    }

    @PatchMapping("/{id}/status/{status}")
    @Operation(summary = "Update the status of a transfer request")
    public ResponseEntity<TransferRequest> updateTransferRequestStatus(
            @Parameter(description = "Transfer request ID") @PathVariable UUID id,
            @Parameter(description = "New status") @PathVariable TransferStatus status) {
        log.info("REST request to update status of transfer request {} to {}", id, status);
        TransferRequest updatedRequest = transferService.updateTransferRequestStatus(id, status);
        return new ResponseEntity<>(updatedRequest, HttpStatus.OK);
    }

    @PatchMapping("/{requestId}/items/{itemId}/status/{status}")
    @Operation(summary = "Update the status of a transfer item")
    public ResponseEntity<TransferRequest> updateTransferItemStatus(
            @Parameter(description = "Transfer request ID") @PathVariable UUID requestId,
            @Parameter(description = "Transfer item ID") @PathVariable UUID itemId,
            @Parameter(description = "New status") @PathVariable TransferItemStatus status) {
        log.info("REST request to update status of transfer item {} to {} in request {}", itemId, status, requestId);
        TransferRequest updatedRequest = transferService.updateTransferItemStatus(requestId, itemId, status);
        return new ResponseEntity<>(updatedRequest, HttpStatus.OK);
    }

    @PatchMapping("/{id}/tracking")
    @Operation(summary = "Update tracking information for a transfer request")
    public ResponseEntity<TransferRequest> updateTrackingInformation(
            @Parameter(description = "Transfer request ID") @PathVariable UUID id,
            @Parameter(description = "Shipping carrier") @RequestParam String carrier,
            @Parameter(description = "Tracking number") @RequestParam String trackingNumber,
            @Parameter(description = "Shipping label URL") @RequestParam(required = false) String labelUrl) {
        log.info("REST request to update tracking information for transfer request: {}", id);
        TransferRequest updatedRequest = transferService.updateTrackingInformation(id, carrier, trackingNumber, labelUrl);
        return new ResponseEntity<>(updatedRequest, HttpStatus.OK);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a transfer request")
    public ResponseEntity<TransferRequest> cancelTransferRequest(
            @Parameter(description = "Transfer request ID") @PathVariable UUID id) {
        log.info("REST request to cancel transfer request: {}", id);
        TransferRequest cancelledRequest = transferService.cancelTransferRequest(id);
        return new ResponseEntity<>(cancelledRequest, HttpStatus.OK);
    }
} 
