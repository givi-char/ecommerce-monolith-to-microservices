package com.givi.ecommerce.productservice.controller;

import com.givi.ecommerce.productservice.dto.InventoryResponse;
import com.givi.ecommerce.productservice.dto.ProductRequest;
import com.givi.ecommerce.productservice.dto.ProductResponse;
import com.givi.ecommerce.productservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<ProductResponse>> getProductsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                productService.getAllProducts(pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }

    @GetMapping("/{id}/inventory")
    public ResponseEntity<InventoryResponse> getProductInventory(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                productService.getInventoryByProductId(id)
        );
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request
    ) {

        ProductResponse createdProduct =
                productService.createProduct(request);

        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {

        ProductResponse updatedProduct =
                productService.updateProduct(id, request);

        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id
    ) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }
}