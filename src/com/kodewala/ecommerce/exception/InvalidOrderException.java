package com.kodewala.ecommerce.exception;

/**
 * Thrown when an order operation is invalid (e.g., cancelling an already delivered order, empty cart checkout).
 */
public class InvalidOrderException extends RuntimeException {

    public InvalidOrderException(String message) {
        super(message);
    }
}
