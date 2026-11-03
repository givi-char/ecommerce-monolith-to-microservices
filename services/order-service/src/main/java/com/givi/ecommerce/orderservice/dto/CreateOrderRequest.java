package com.givi.ecommerce.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CreateOrderRequest {

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<CreateOrderItemRequest> items;

    public List<CreateOrderItemRequest> getItems() {
        return items;
    }

    public void setItems(
            List<CreateOrderItemRequest> items
    ) {
        this.items = items;
    }
}