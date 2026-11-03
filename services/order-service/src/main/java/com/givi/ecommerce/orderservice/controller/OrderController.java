package com.givi.ecommerce.orderservice.controller;

import com.givi.ecommerce.orderservice.dto.CreateOrderRequest;
import com.givi.ecommerce.orderservice.dto.OrderResponse;
import com.givi.ecommerce.orderservice.security.OrderUserPrincipal;
import com.givi.ecommerce.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
            @AuthenticationPrincipal
            OrderUserPrincipal principal,

            @Valid
            @RequestBody
            CreateOrderRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        orderService.createOrder(
                                principal.getUserId(),
                                request
                        )
                );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse>
    getOrderById(
            @PathVariable Long orderId,

            @AuthenticationPrincipal
            OrderUserPrincipal principal,

            Authentication authentication
    ) {

        OrderResponse order =
                orderService.getOrderById(
                        orderId
                );

        /*
         * ADMIN-ს შეუძლია ნებისმიერი order-ის ნახვა.
         *
         * ჩვეულებრივ მომხმარებელს მხოლოდ თავისი
         * order-ის ნახვა შეუძლია.
         */
        if (!isAdmin(authentication)
                && !order.getUserId().equals(
                principal.getUserId()
        )) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to access this order"
            );
        }

        return ResponseEntity.ok(
                order
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>>
    getOrdersByUser(
            @PathVariable Long userId,

            @AuthenticationPrincipal
            OrderUserPrincipal principal,

            Authentication authentication
    ) {

        /*
         * USER-ს სხვისი userId-ის გამოყენება
         * აღარ შეუძლია.
         *
         * ADMIN-ს შეუძლია ნებისმიერი user-ის
         * orders ნახოს.
         */
        validateUserAccess(
                userId,
                principal,
                authentication
        );

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
            int size,

            @AuthenticationPrincipal
            OrderUserPrincipal principal,

            Authentication authentication
    ) {

        validateUserAccess(
                userId,
                principal,
                authentication
        );

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
            int size,

            Authentication authentication
    ) {

        /*
         * ყველა მომხმარებლის orders-ის ნახვა
         * მხოლოდ ADMIN-ს შეუძლია.
         */
        if (!isAdmin(authentication)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only administrators can access all orders"
            );
        }

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

    private void validateUserAccess(
            Long requestedUserId,
            OrderUserPrincipal principal,
            Authentication authentication
    ) {

        if (isAdmin(authentication)) {
            return;
        }

        if (!requestedUserId.equals(
                principal.getUserId()
        )) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to access another user's orders"
            );
        }
    }

    private boolean isAdmin(
            Authentication authentication
    ) {

        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(
                                authority.getAuthority()
                        )
                );
    }
}