package com.kodewala.ecommerce.service;

import com.kodewala.ecommerce.exception.InsufficientStockException;
import com.kodewala.ecommerce.exception.InvalidOrderException;
import com.kodewala.ecommerce.model.Order;
import java.util.List;

/**
 * ============================================================================
 * SOLID Principles:
 * - ISP (Interface Segregation Principle): Focused interface for Order operations.
 * - DIP (Dependency Inversion Principle): High-level modules depend on abstractions.
 * ============================================================================
 */
public interface OrderService {

    // Places an order from the customer's cart, reduces inventory, and clears the cart
    Order placeOrder(int customerId) throws InvalidOrderException, InsufficientStockException;

    // Retrieves order details by order ID
    Order getOrderById(int orderId) throws InvalidOrderException;

    // Retrieves all orders placed by a specific customer (Order History)
    List<Order> getOrdersByCustomerId(int customerId);

    // Retrieves all orders across the system (Admin)
    List<Order> getAllOrders();

    // Cancels an existing order and restores product stock
    void cancelOrder(int orderId) throws InvalidOrderException;
}
