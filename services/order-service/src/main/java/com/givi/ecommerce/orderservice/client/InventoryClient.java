package com.givi.ecommerce.orderservice.client;

import com.givi.ecommerce.orderservice.dto.InventoryResponse;
import com.givi.ecommerce.orderservice.dto.UpdateStockRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient(
            @Value("${services.inventory.base-url}")
            String inventoryBaseUrl
    ) {

        this.restClient =
                RestClient.builder()
                        .baseUrl(inventoryBaseUrl)
                        .build();
    }

    public InventoryResponse reduceStock(
            Long productId,
            Integer quantity
    ) {

        UpdateStockRequest request =
                new UpdateStockRequest(quantity);

        try {

            return restClient
                    .put()
                    .uri(
                            "/api/v1/inventory/{productId}/reduce",
                            productId
                    )
                    .body(request)
                    .retrieve()
                    .body(InventoryResponse.class);

        } catch (RestClientException exception) {

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Inventory Service is currently unavailable",
                    exception
            );
        }
    }
}