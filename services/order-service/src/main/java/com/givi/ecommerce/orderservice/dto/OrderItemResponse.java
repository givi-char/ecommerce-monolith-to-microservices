package com.givi.ecommerce.orderservice.dto;

import com.givi.ecommerce.orderservice.common.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private Long id;

    private Long productId;

    private int quantity;

    private Money price;
}