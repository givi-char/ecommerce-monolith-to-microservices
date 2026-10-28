package com.givi.ecommerce.productservice.exception;

public class InventoryServiceUnavailableException extends RuntimeException {

    public InventoryServiceUnavailableException(String message) {
        super(message);
    }
}
