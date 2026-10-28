package com.givi.ecommerce.productservice.service.impl;

import com.givi.ecommerce.productservice.client.InventoryClient;
import com.givi.ecommerce.productservice.dto.InventoryResponse;
import com.givi.ecommerce.productservice.dto.ProductRequest;
import com.givi.ecommerce.productservice.dto.ProductResponse;
import com.givi.ecommerce.productservice.entity.Brand;
import com.givi.ecommerce.productservice.entity.Category;
import com.givi.ecommerce.productservice.entity.Product;
import com.givi.ecommerce.productservice.exception.DuplicateResourceException;
import com.givi.ecommerce.productservice.exception.ResourceNotFoundException;
import com.givi.ecommerce.productservice.mapper.ProductMapper;
import com.givi.ecommerce.productservice.repository.BrandRepository;
import com.givi.ecommerce.productservice.repository.CategoryRepository;
import com.givi.ecommerce.productservice.repository.ProductRepository;
import com.givi.ecommerce.productservice.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;
    private final InventoryClient inventoryClient;

    public ProductServiceImpl(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            BrandRepository brandRepository,
            ProductMapper productMapper,
            InventoryClient inventoryClient
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.productMapper = productMapper;
        this.inventoryClient = inventoryClient;
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {

        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException(
                    "Product with SKU " + request.getSku() + " already exists"
            );
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + request.getCategoryId()
                        )
                );

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Brand not found with id: " + request.getBrandId()
                        )
                );

        Product product = productMapper.toEntity(
                request,
                category,
                brand
        );

        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        if (!product.getSku().equals(request.getSku())
                && productRepository.existsBySku(request.getSku())) {

            throw new DuplicateResourceException(
                    "Product with SKU " + request.getSku() + " already exists"
            );
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + request.getCategoryId()
                        )
                );

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Brand not found with id: " + request.getBrandId()
                        )
                );

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSku(request.getSku());
        product.setImageUrl(request.getImageUrl());
        product.setStatus(request.getStatus());
        product.setCategory(category);
        product.setBrand(brand);
        product.setSellerUserId(request.getSellerUserId());

        Product updatedProduct = productRepository.save(product);

        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        productRepository.delete(product);
    }

    @Override
    public InventoryResponse getInventoryByProductId(Long productId) {

        productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + productId
                        )
                );

        return inventoryClient.getInventoryByProductId(productId);
    }
}