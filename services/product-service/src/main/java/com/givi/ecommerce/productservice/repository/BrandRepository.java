package com.givi.ecommerce.productservice.repository;

import com.givi.ecommerce.productservice.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByName(String name);

    boolean existsByName(String name);
}