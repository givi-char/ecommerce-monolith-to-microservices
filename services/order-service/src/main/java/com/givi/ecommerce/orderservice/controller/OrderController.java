package com.givi.ecommerce.orderservice.controller;

import com.givi.ecommerce.orderservice.dto.CreateOrderRequest;
import com.givi.ecommerce.orderservice.dto.OrderResponse;
import com.givi.ecommerce.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(
            OrderService orderService
    ) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse>
    createOrder(
            @Valid
            @RequestBody
            CreateOrderRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        orderService.createOrder(
                                request
                        )
                );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse>
    getOrderById(
            @PathVariable Long orderId
    ) {

        return ResponseEntity.ok(
                orderService.getOrderById(
                        orderId
                )
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>>
    getOrdersByUser(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                orderService.getOrdersByUser(
                        userId
                )
        );
    }

    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<Page<OrderResponse>>
    getOrdersByUserPaged(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );

        return ResponseEntity.ok(
                orderService.getOrdersByUser(
                        userId,
                        pageable
                )
        );
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<OrderResponse>>
    getAllOrders(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );

        return ResponseEntity.ok(
                orderService.getAllOrders(
                        pageable
                )
        );
    }
}