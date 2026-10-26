package com.givi.ecommerce.productservice.mapper;

import com.givi.ecommerce.productservice.dto.BrandResponse;
import com.givi.ecommerce.productservice.entity.Brand;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {

    public BrandResponse toResponse(Brand brand) {
        return new BrandResponse(
                brand.getId(),
                brand.getName(),
                brand.getDescription(),
                brand.getCountry(),
                brand.getStatus()
        );
    }
}