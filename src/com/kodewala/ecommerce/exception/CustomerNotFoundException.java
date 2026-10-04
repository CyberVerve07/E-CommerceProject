package com.kodewala.ecommerce.exception;

/**
 * Thrown when a requested customer is not found by ID or credentials.
 */
public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String message) {
        super(message);
    }
}
