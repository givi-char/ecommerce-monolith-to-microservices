package com.givi.ecommerce.orderservice.service.impl;

import com.givi.ecommerce.orderservice.client.InventoryClient;
import com.givi.ecommerce.orderservice.common.Money;
import com.givi.ecommerce.orderservice.dto.CreateOrderItemRequest;
import com.givi.ecommerce.orderservice.dto.CreateOrderRequest;
import com.givi.ecommerce.orderservice.dto.OrderResponse;
import com.givi.ecommerce.orderservice.entity.Order;
import com.givi.ecommerce.orderservice.entity.OrderItem;
import com.givi.ecommerce.orderservice.enums.OrderStatus;
import com.givi.ecommerce.orderservice.exception.OrderNotFoundException;
import com.givi.ecommerce.orderservice.mapper.OrderMapper;
import com.givi.ecommerce.orderservice.repository.OrderRepository;
import com.givi.ecommerce.orderservice.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl
        implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final InventoryClient inventoryClient;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            InventoryClient inventoryClient
    ) {

        this.orderRepository =
                orderRepository;

        this.orderMapper =
                orderMapper;

        this.inventoryClient =
                inventoryClient;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(
            CreateOrderRequest request
    ) {

        Order order =
                new Order();

        order.setUserId(
                request.getUserId()
        );

        order.setStatus(
                OrderStatus.CREATED
        );

        List<OrderItem> orderItems =
                new ArrayList<>();

        BigDecimal totalAmount =
                BigDecimal.ZERO;

        String orderCurrency =
                null;

        for (CreateOrderItemRequest itemRequest
                : request.getItems()) {

            String itemCurrency =
                    itemRequest
                            .getCurrency()
                            .toUpperCase();

            if (orderCurrency == null) {

                orderCurrency =
                        itemCurrency;
            }

            if (!orderCurrency.equals(
                    itemCurrency
            )) {

                throw new IllegalArgumentException(
                        "All order items must use the same currency"
                );
            }

            inventoryClient.reduceStock(
                    itemRequest.getProductId(),
                    itemRequest.getQuantity()
            );

            OrderItem orderItem =
                    new OrderItem();

            orderItem.setOrder(
                    order
            );

            orderItem.setProductId(
                    itemRequest.getProductId()
            );

            orderItem.setQuantity(
                    itemRequest.getQuantity()
            );

            orderItem.setPrice(
                    new Money(
                            itemRequest
                                    .getPriceAmount(),
                            itemCurrency
                    )
            );

            orderItems.add(
                    orderItem
            );

            BigDecimal itemTotal =
                    itemRequest
                            .getPriceAmount()
                            .multiply(
                                    BigDecimal.valueOf(
                                            itemRequest
                                                    .getQuantity()
                                    )
                            );

            totalAmount =
                    totalAmount.add(
                            itemTotal
                    );
        }

        order.setItems(
                orderItems
        );

        order.setTotalAmount(
                new Money(
                        totalAmount,
                        orderCurrency
                )
        );

        Order savedOrder =
                orderRepository.save(
                        order
                );

        return orderMapper.toResponse(
                savedOrder
        );
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(
            Long orderId
    ) {

        Order order =
                orderRepository
                        .findById(
                                orderId
                        )
                        .orElseThrow(() ->
                                new OrderNotFoundException(
                                        orderId
                                )
                        );

        return orderMapper.toResponse(
                order
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(
            Long userId
    ) {

        return orderRepository
                .findByUserId(
                        userId
                )
                .stream()
                .map(
                        orderMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUser(
            Long userId,
            Pageable pageable
    ) {

        return orderRepository
                .findByUserId(
                        userId,
                        pageable
                )
                .map(
                        orderMapper::toResponse
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(
            Pageable pageable
    ) {

        return orderRepository
                .findAll(
                        pageable
                )
                .map(
                        orderMapper::toResponse
                );
    }
}