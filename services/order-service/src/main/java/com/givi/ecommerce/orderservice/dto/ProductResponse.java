package com.givi.ecommerce.orderservice.dto;

import java.math.BigDecimal;

public class ProductResponse {

    private Long id;
    private String name;
    private MoneyResponse price;
    private String status;

    public ProductResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MoneyResponse getPrice() {
        return price;
    }

    public void setPrice(MoneyResponse price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class MoneyResponse {

        private BigDecimal amount;
        private String currency;

        public MoneyResponse() {
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }
}