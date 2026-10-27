package com.givi.ecommerce.productservice.service;

import com.givi.ecommerce.productservice.dto.BrandResponse;

import java.util.List;
import java.util.Optional;

public interface BrandService {

    List<BrandResponse> getAllBrands();

    Optional<BrandResponse> getBrandById(Long id);
}