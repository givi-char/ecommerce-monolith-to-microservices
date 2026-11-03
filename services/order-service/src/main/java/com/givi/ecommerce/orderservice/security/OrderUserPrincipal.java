package com.givi.ecommerce.orderservice.security;

public class OrderUserPrincipal {

    private final Long userId;
    private final String email;

    public OrderUserPrincipal(
            Long userId,
            String email
    ) {
        this.userId = userId;
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return email;
    }
}