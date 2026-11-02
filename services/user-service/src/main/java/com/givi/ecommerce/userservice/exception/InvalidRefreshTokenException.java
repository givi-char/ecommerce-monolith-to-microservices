package com.givi.ecommerce.userservice.exception;

public class InvalidRefreshTokenException
        extends RuntimeException {

    public InvalidRefreshTokenException(
            String message
    ) {
        super(message);
    }
}