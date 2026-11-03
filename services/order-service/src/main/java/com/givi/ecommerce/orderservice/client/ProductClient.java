package com.givi.ecommerce.orderservice.client;

import com.givi.ecommerce.orderservice.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(
            @Value("${services.product.base-url}")
            String productBaseUrl
    ) {
        this.restClient =
                RestClient.builder()
                        .baseUrl(productBaseUrl)
                        .build();
    }

    public ProductResponse getProductById(Long productId) {

        try {

            ProductResponse product =
                    restClient
                            .get()
                            .uri(
                                    "/api/v1/products/{productId}",
                                    productId
                            )
                            .retrieve()
                            .body(ProductResponse.class);

            if (product == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found: " + productId
                );
            }

            return product;

        } catch (ResourceAccessException exception) {

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Product Service is currently unavailable",
                    exception
            );

        } catch (RestClientResponseException exception) {

            if (exception.getStatusCode().value() == 404) {

                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found: " + productId,
                        exception
                );
            }

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Product Service returned an unexpected error",
                    exception
            );
        }
    }
}