package com.givi.ecommerce.productservice.mapper;

import com.givi.ecommerce.productservice.dto.CategoryResponse;
import com.givi.ecommerce.productservice.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getStatus()
        );
    }
}