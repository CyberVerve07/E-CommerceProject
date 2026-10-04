package com.kodewala.ecommerce.exception;

/**
 * Thrown when a requested product cannot be found by ID or other criteria.
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}
