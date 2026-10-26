package com.givi.ecommerce.productservice.service;

import com.givi.ecommerce.productservice.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> getAllCategories();

    Optional<Category> getCategoryById(Long id);
}