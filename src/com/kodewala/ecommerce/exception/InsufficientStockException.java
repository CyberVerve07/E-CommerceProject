package com.kodewala.ecommerce.exception;

/**
 * Thrown when an order or cart addition requests more product quantity than available stock.
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }
}
