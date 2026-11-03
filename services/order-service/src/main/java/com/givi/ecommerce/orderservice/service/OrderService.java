package com.givi.ecommerce.orderservice.service;

import com.givi.ecommerce.orderservice.dto.CreateOrderRequest;
import com.givi.ecommerce.orderservice.dto.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(
            CreateOrderRequest request
    );

    OrderResponse getOrderById(
            Long orderId
    );

    List<OrderResponse> getOrdersByUser(
            Long userId
    );

    Page<OrderResponse> getOrdersByUser(
            Long userId,
            Pageable pageable
    );

    Page<OrderResponse> getAllOrders(
            Pageable pageable
    );
}