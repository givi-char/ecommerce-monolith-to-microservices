package com.givi.ecommerce.productservice.service.impl;

import com.givi.ecommerce.productservice.dto.ProductResponse;
import com.givi.ecommerce.productservice.mapper.ProductMapper;
import com.givi.ecommerce.productservice.repository.ProductRepository;
import com.givi.ecommerce.productservice.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(
            ProductRepository productRepository,
            ProductMapper productMapper
    ) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    public Optional<ProductResponse> getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse);
    }
}