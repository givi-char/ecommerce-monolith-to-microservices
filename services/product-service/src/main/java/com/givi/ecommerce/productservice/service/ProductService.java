package com.givi.ecommerce.productservice.service;

import com.givi.ecommerce.productservice.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductService {

    Page<ProductResponse> getAllProducts(Pageable pageable);

    Optional<ProductResponse> getProductById(Long id);
}