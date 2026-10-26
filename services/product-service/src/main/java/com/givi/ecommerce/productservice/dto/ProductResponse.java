package com.givi.ecommerce.productservice.dto;

import com.givi.ecommerce.productservice.common.Money;
import com.givi.ecommerce.productservice.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private Money price;
    private String sku;
    private String imageUrl;
    private ProductStatus status;

    private Long categoryId;
    private String categoryName;

    private Long brandId;
    private String brandName;

    private Long sellerUserId;
}