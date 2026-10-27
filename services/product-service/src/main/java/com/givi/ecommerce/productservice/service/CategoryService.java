package com.givi.ecommerce.productservice.service;

import com.givi.ecommerce.productservice.dto.CategoryResponse;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<CategoryResponse> getAllCategories();

    Optional<CategoryResponse> getCategoryById(Long id);
}