package com.givi.ecommerce.productservice.mapper;

import com.givi.ecommerce.productservice.dto.ProductRequest;
import com.givi.ecommerce.productservice.dto.ProductResponse;
import com.givi.ecommerce.productservice.entity.Brand;
import com.givi.ecommerce.productservice.entity.Category;
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

    public Product toEntity(
            ProductRequest request,
            Category category,
            Brand brand
    ) {

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSku(request.getSku());
        product.setImageUrl(request.getImageUrl());
        product.setStatus(request.getStatus());
        product.setCategory(category);
        product.setBrand(brand);
        product.setSellerUserId(request.getSellerUserId());

        return product;
    }
}