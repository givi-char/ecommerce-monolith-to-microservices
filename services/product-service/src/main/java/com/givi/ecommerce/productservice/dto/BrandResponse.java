package com.givi.ecommerce.productservice.dto;

import com.givi.ecommerce.productservice.enums.BrandStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponse {

    private Long id;
    private String name;
    private String description;
    private String country;
    private BrandStatus status;
}