package com.givi.ecommerce.productservice.mapper;

import com.givi.ecommerce.productservice.dto.ProductResponse;
import com.givi.ecommerce.productservice.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getSku(),
                product.getImageUrl(),
                product.getStatus(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getBrand() != null ? product.getBrand().getId() : null,
                product.getBrand() != null ? product.getBrand().getName() : null,
                product.getSellerUserId()
        );
    }
}