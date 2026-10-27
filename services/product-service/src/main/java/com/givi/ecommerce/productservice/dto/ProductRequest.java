package com.givi.ecommerce.productservice.dto;

import com.givi.ecommerce.productservice.common.Money;
import com.givi.ecommerce.productservice.enums.ProductStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Valid
    private Money price;

    @NotBlank(message = "SKU is required")
    private String sku;

    private String imageUrl;

    @NotNull(message = "Product status is required")
    private ProductStatus status;

    @NotNull(message = "Category id is required")
    @Positive(message = "Category id must be positive")
    private Long categoryId;

    @NotNull(message = "Brand id is required")
    @Positive(message = "Brand id must be positive")
    private Long brandId;

    private Long sellerUserId;
}