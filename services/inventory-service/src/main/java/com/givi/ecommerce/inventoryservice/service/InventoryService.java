package com.givi.ecommerce.inventoryservice.service;

import com.givi.ecommerce.inventoryservice.dto.request.UpdateStockRequest;
import com.givi.ecommerce.inventoryservice.dto.response.InventoryResponse;

public interface InventoryService {

    InventoryResponse getInventoryByProductId(Long productId);

    InventoryResponse increaseStock(Long productId, UpdateStockRequest request);

    InventoryResponse reduceStock(Long productId, UpdateStockRequest request);
}