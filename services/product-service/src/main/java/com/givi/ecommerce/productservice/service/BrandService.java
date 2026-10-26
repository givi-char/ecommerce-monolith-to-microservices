package com.givi.ecommerce.productservice.service;

import com.givi.ecommerce.productservice.entity.Brand;

import java.util.List;
import java.util.Optional;

public interface BrandService {

    List<Brand> getAllBrands();

    Optional<Brand> getBrandById(Long id);
}