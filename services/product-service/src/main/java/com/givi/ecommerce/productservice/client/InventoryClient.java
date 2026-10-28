package com.givi.ecommerce.productservice.client;

import com.givi.ecommerce.productservice.dto.InventoryResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient() {
        this.restClient = RestClient.create("http://localhost:8082");
    }

    public InventoryResponse getInventoryByProductId(Long productId) {

        return restClient.get()
                .uri("/api/v1/inventory/{productId}", productId)
                .retrieve()
                .body(InventoryResponse.class);
    }
}