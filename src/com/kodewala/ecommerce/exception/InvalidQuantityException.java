package com.kodewala.ecommerce.exception;

/**
 * Thrown when an invalid quantity is provided (e.g., negative or zero quantity).
 */
public class InvalidQuantityException extends RuntimeException {

    public InvalidQuantityException(String message) {
        super(message);
    }
}
