package com.givi.ecommerce.productservice.service.impl;

import com.givi.ecommerce.productservice.entity.Brand;
import com.givi.ecommerce.productservice.repository.BrandRepository;
import com.givi.ecommerce.productservice.service.BrandService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public Optional<Brand> getBrandById(Long id) {
        return brandRepository.findById(id);
    }
}