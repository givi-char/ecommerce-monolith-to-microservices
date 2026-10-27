package com.givi.ecommerce.inventoryservice.controller;

import com.givi.ecommerce.inventoryservice.dto.request.UpdateStockRequest;
import com.givi.ecommerce.inventoryservice.dto.response.InventoryResponse;
import com.givi.ecommerce.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(
            @PathVariable Long productId
    ) {

        return ResponseEntity.ok(
                inventoryService.getInventoryByProductId(productId)
        );
    }

    @PutMapping("/{productId}/increase")
    public ResponseEntity<InventoryResponse> increaseStock(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateStockRequest request
    ) {

        return ResponseEntity.ok(
                inventoryService.increaseStock(productId, request)
        );
    }

    @PutMapping("/{productId}/reduce")
    public ResponseEntity<InventoryResponse> reduceStock(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateStockRequest request
    ) {

        return ResponseEntity.ok(
                inventoryService.reduceStock(productId, request)
        );
    }
}