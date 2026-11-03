package com.givi.ecommerce.orderservice.enums;

/**
 * Order status workflow:
 * USER: CREATED -> PLACED -> PAID
 * SELLER: PAID -> CONFIRMED -> SHIPPED -> DELIVERED
 * Or: CREATED/PLACED/PAID -> CANCELLED/REFUNDED
 */
public enum OrderStatus {

    CREATED,
    PLACED,
    PAID,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}