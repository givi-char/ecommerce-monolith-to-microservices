package com.givi.ecommerce.orderservice.client;

import com.givi.ecommerce.orderservice.dto.InventoryResponse;
import com.givi.ecommerce.orderservice.dto.UpdateStockRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient(
            @Value("${services.inventory.base-url}")
            String inventoryBaseUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(inventoryBaseUrl)
                .build();
    }

    public InventoryResponse reduceStock(
            Long productId,
            Integer quantity
    ) {
        return updateStock(productId, quantity, "reduce");
    }

    public InventoryResponse increaseStock(
            Long productId,
            Integer quantity
    ) {
        return updateStock(productId, quantity, "increase");
    }

    private InventoryResponse updateStock(
            Long productId,
            Integer quantity,
            String operation
    ) {
        UpdateStockRequest request =
                new UpdateStockRequest(quantity);

        try {
            return restClient.put()
                    .uri(
                            "/api/v1/inventory/{productId}/{operation}",
                            productId,
                            operation
                    )
                    .body(request)
                    .retrieve()
                    .body(InventoryResponse.class);

        } catch (RestClientResponseException exception) {

            int status = exception.getStatusCode().value();

            if (status == 409) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Inventory conflict for product id: " + productId,
                        exception
                );
            }

            if (status == 404) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Inventory not found for product id: " + productId,
                        exception
                );
            }

            if (status == 400) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid stock update request",
                        exception
                );
            }

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Inventory Service returned an unexpected error",
                    exception
            );

        } catch (ResourceAccessException exception) {

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Cannot connect to Inventory Service",
                    exception
            );

        } catch (RestClientException exception) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Could not process Inventory Service response",
                    exception
            );
        }
    }
}