package com.givi.ecommerce.inventoryservice.mapper;

import com.givi.ecommerce.inventoryservice.dto.response.InventoryResponse;
import com.givi.ecommerce.inventoryservice.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryResponse toResponse(Inventory inventory) {

        return new InventoryResponse(
                inventory.getId(),
                inventory.getProductId(),
                inventory.getQuantity()
        );
    }
}