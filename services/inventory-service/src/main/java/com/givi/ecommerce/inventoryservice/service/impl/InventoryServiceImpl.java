package com.givi.ecommerce.inventoryservice.service.impl;

import com.givi.ecommerce.inventoryservice.dto.request.UpdateStockRequest;
import com.givi.ecommerce.inventoryservice.dto.response.InventoryResponse;
import com.givi.ecommerce.inventoryservice.entity.Inventory;
import com.givi.ecommerce.inventoryservice.exception.InsufficientStockException;
import com.givi.ecommerce.inventoryservice.exception.InventoryNotFoundException;
import com.givi.ecommerce.inventoryservice.mapper.InventoryMapper;
import com.givi.ecommerce.inventoryservice.repository.InventoryRepository;
import com.givi.ecommerce.inventoryservice.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    public InventoryServiceImpl(
            InventoryRepository inventoryRepository,
            InventoryMapper inventoryMapper
    ) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByProductId(
            Long productId
    ) {
        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(
                                "Inventory not found for product id: "
                                        + productId
                        )
                );

        return inventoryMapper.toResponse(inventory);
    }

    @Override
    @Transactional
    public InventoryResponse increaseStock(
            Long productId,
            UpdateStockRequest request
    ) {
        Inventory inventory = findInventoryForUpdate(productId);

        inventory.setQuantity(
                inventory.getQuantity() + request.getQuantity()
        );

        Inventory updatedInventory =
                inventoryRepository.save(inventory);

        return inventoryMapper.toResponse(updatedInventory);
    }

    @Override
    @Transactional
    public InventoryResponse reduceStock(
            Long productId,
            UpdateStockRequest request
    ) {
        Inventory inventory = findInventoryForUpdate(productId);

        if (inventory.getQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock for product id: " + productId
            );
        }

        inventory.setQuantity(
                inventory.getQuantity() - request.getQuantity()
        );

        Inventory updatedInventory =
                inventoryRepository.save(inventory);

        return inventoryMapper.toResponse(updatedInventory);
    }

    private Inventory findInventoryForUpdate(Long productId) {
        return inventoryRepository
                .findByProductIdForUpdate(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(
                                "Inventory not found for product id: "
                                        + productId
                        )
                );
    }
}