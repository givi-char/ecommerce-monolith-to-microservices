package com.givi.ecommerce.orderservice.service.impl;

import com.givi.ecommerce.orderservice.client.InventoryClient;
import com.givi.ecommerce.orderservice.client.ProductClient;
import com.givi.ecommerce.orderservice.common.Money;
import com.givi.ecommerce.orderservice.dto.CreateOrderItemRequest;
import com.givi.ecommerce.orderservice.dto.CreateOrderRequest;
import com.givi.ecommerce.orderservice.dto.OrderResponse;
import com.givi.ecommerce.orderservice.dto.ProductResponse;
import com.givi.ecommerce.orderservice.entity.Order;
import com.givi.ecommerce.orderservice.entity.OrderItem;
import com.givi.ecommerce.orderservice.enums.OrderStatus;
import com.givi.ecommerce.orderservice.exception.OrderNotFoundException;
import com.givi.ecommerce.orderservice.mapper.OrderMapper;
import com.givi.ecommerce.orderservice.repository.OrderRepository;
import com.givi.ecommerce.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log =
            LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final InventoryClient inventoryClient;
    private final ProductClient productClient;
    private final TransactionTemplate transactionTemplate;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            InventoryClient inventoryClient,
            ProductClient productClient,
            PlatformTransactionManager transactionManager
    ) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.inventoryClient = inventoryClient;
        this.productClient = productClient;

        this.transactionTemplate =
                new TransactionTemplate(transactionManager);

        this.transactionTemplate.setPropagationBehavior(
                TransactionDefinition.PROPAGATION_REQUIRES_NEW
        );
    }

    @Override
    public OrderResponse createOrder(
            Long userId,
            CreateOrderRequest request
    ) {
        List<StockReduction> reductions = new ArrayList<>();

        try {
            return transactionTemplate.execute(status ->
                    createOrderWithinTransaction(
                            userId,
                            request,
                            reductions
                    )
            );
        } catch (RuntimeException orderFailure) {

            boolean restored = restoreStock(reductions, orderFailure);

            if (!restored) {
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Order failed and stock restoration was incomplete. "
                                + "Manual reconciliation is required.",
                        orderFailure
                );
            }

            throw orderFailure;
        }
    }

    private OrderResponse createOrderWithinTransaction(
            Long userId,
            CreateOrderRequest request,
            List<StockReduction> reductions
    ) {
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CREATED);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        String orderCurrency = null;

        for (CreateOrderItemRequest itemRequest : request.getItems()) {

            ProductResponse product =
                    productClient.getProductById(
                            itemRequest.getProductId()
                    );

            if (product.getStatus() == null
                    || !"ACTIVE".equalsIgnoreCase(product.getStatus())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Product is not active: "
                                + itemRequest.getProductId()
                );
            }

            if (product.getPrice() == null
                    || product.getPrice().getAmount() == null
                    || product.getPrice().getCurrency() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Product Service returned invalid price for product: "
                                + itemRequest.getProductId()
                );
            }

            BigDecimal productPrice = product.getPrice().getAmount();
            String productCurrency = product.getPrice()
                    .getCurrency()
                    .toUpperCase(Locale.ROOT);

            if (orderCurrency == null) {
                orderCurrency = productCurrency;
            }

            if (!orderCurrency.equals(productCurrency)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "All order items must use the same currency"
                );
            }

            inventoryClient.reduceStock(
                    itemRequest.getProductId(),
                    itemRequest.getQuantity()
            );

            reductions.add(
                    new StockReduction(
                            itemRequest.getProductId(),
                            itemRequest.getQuantity()
                    )
            );

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(itemRequest.getProductId());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(
                    new Money(productPrice, productCurrency)
            );

            orderItems.add(orderItem);

            totalAmount = totalAmount.add(
                    productPrice.multiply(
                            BigDecimal.valueOf(itemRequest.getQuantity())
                    )
            );
        }

        order.setItems(orderItems);
        order.setTotalAmount(
                new Money(totalAmount, orderCurrency)
        );

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    private boolean restoreStock(
            List<StockReduction> reductions,
            RuntimeException orderFailure
    ) {
        boolean restored = true;

        for (int i = reductions.size() - 1; i >= 0; i--) {
            StockReduction reduction = reductions.get(i);

            try {
                inventoryClient.increaseStock(
                        reduction.productId(),
                        reduction.quantity()
                );

                log.info(
                        "Restored stock: productId={}, quantity={}",
                        reduction.productId(),
                        reduction.quantity()
                );

            } catch (RuntimeException restorationFailure) {
                restored = false;
                orderFailure.addSuppressed(restorationFailure);

                log.error(
                        "Stock restoration failed: productId={}, quantity={}. "
                                + "Manual reconciliation is required.",
                        reduction.productId(),
                        reduction.quantity(),
                        restorationFailure
                );
            }
        }

        return restored;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(orderId)
                );

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUser(
            Long userId,
            Pageable pageable
    ) {
        return orderRepository.findByUserId(userId, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toResponse);
    }

    private record StockReduction(Long productId, int quantity) {
    }
}