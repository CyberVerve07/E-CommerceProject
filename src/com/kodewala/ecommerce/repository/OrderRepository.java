package com.kodewala.ecommerce.repository;

import com.kodewala.ecommerce.model.Order;
import com.kodewala.ecommerce.model.OrderStatus;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * SOLID Principles:
 * - ISP (Interface Segregation Principle): Dedicated interface for Order persistence.
 * - DIP (Dependency Inversion Principle): Services depend on this abstraction.
 * ============================================================================
 */
public interface OrderRepository {

    // Saves an order into the order collection (e.g., List<Order>)
    void saveOrder(Order order);

    // Retrieves an order by its unique order ID
    Optional<Order> findById(int orderId);

    // Retrieves all orders across the entire application (for Admin)
    List<Order> getAllOrders();

    // Retrieves all orders placed by a specific customer (for Order History)
    List<Order> findByCustomerId(int customerId);

    // Updates the status of an order (e.g., to CANCELLED)
    boolean updateOrderStatus(int orderId, OrderStatus newStatus);

    // Generates the next unique order ID
    int getNextOrderId();
}
