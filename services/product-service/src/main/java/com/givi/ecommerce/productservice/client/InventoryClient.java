package com.givi.ecommerce.productservice.client;

import com.givi.ecommerce.productservice.dto.InventoryResponse;
import com.givi.ecommerce.productservice.exception.InventoryServiceUnavailableException;
import com.givi.ecommerce.productservice.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient() {
        this.restClient = RestClient.create("http://localhost:8082");
    }

    public InventoryResponse getInventoryByProductId(Long productId) {

        try {

            return restClient.get()
                    .uri("/api/v1/inventory/{productId}", productId)
                    .retrieve()
                    .body(InventoryResponse.class);

        } catch (HttpClientErrorException.NotFound ex) {

            throw new ResourceNotFoundException(
                    "Inventory not found for product id: " + productId
            );

        } catch (ResourceAccessException ex) {

            throw new InventoryServiceUnavailableException(
                    "Inventory Service is currently unavailable"
            );
        }
    }
}