package com.givi.ecommerce.productservice.service.impl;

import com.givi.ecommerce.productservice.dto.BrandResponse;
import com.givi.ecommerce.productservice.mapper.BrandMapper;
import com.givi.ecommerce.productservice.repository.BrandRepository;
import com.givi.ecommerce.productservice.service.BrandService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public BrandServiceImpl(
            BrandRepository brandRepository,
            BrandMapper brandMapper
    ) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
    }

    @Override
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll()
                .stream()
                .map(brandMapper::toResponse)
                .toList();
    }

    @Override
    public Optional<BrandResponse> getBrandById(Long id) {
        return brandRepository.findById(id)
                .map(brandMapper::toResponse);
    }
}