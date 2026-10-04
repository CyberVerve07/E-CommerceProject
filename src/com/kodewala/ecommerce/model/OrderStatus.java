package com.kodewala.ecommerce.model;

/**
 * ============================================================================
 * SOLID Principle: Single Responsibility Principle (SRP)
 * Represents the lifecycle statuses of an order in the e-commerce system.
 * Possible statuses: PLACED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED.
 * ============================================================================
 */
public enum OrderStatus {
    PLACED,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
