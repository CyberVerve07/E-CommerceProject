package com.kodewala.ecommerce.repository.impl;

import com.kodewala.ecommerce.model.Order;
import com.kodewala.ecommerce.model.OrderStatus;
import com.kodewala.ecommerce.repository.OrderRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class Name: OrderRepositoryImpl
 * Layer: Repository / Data Access Layer
 *
 * Description:
 * Implements in-memory data storage for Orders.
 *
 * Concepts Used:
 * 1. Java Collections (ArrayList<Order>):
 *    - Stores historical order entries in an ordered list as required by specification.
 * 2. Java 8 Stream API:
 *    - Uses filter() and collect(Collectors.toList()) to retrieve orders by customer ID.
 * 3. Auto-increment Sequence:
 *    - Simulates database auto-incrementing primary key ID for new orders.
 */
public class OrderRepositoryImpl implements OrderRepository {

    // Main collection to store placed orders
    private final List<Order> orderList = new ArrayList<>();

    // Internal sequence generator starting from 1001
    private int orderIdSequence = 1001;

    /**
     * Concept: List append
     * Why: Preserves chronological order of placed orders.
     */
    @Override
    public void saveOrder(Order order) {
        if (order != null) {
            orderList.add(order);
        }
    }

    /**
     * Concept: Java 8 Stream filter & findFirst with Optional
     * Why: Safely searches order by ID.
     */
    @Override
    public Optional<Order> findById(int orderId) {
        return orderList.stream()
                .filter(order -> order.getOrderId() == orderId)
                .findFirst();
    }

    /**
     * Concept: Defensive Copy
     * Why: Returning a new ArrayList preserves internal encapsulation.
     */
    @Override
    public List<Order> getAllOrders() {
        return new ArrayList<>(orderList);
    }

    /**
     * Concept: Java 8 Stream Filtering & Collecting
     * Why: Finds all orders belonging to a specific customer without mutating the original list.
     */
    @Override
    public List<Order> findByCustomerId(int customerId) {
        return orderList.stream()
                .filter(order -> order.getCustomerId() == customerId)
                .collect(Collectors.toList());
    }

    /**
     * Concept: State Mutation via Optional
     * Why: Updates order state safely if the order is found.
     */
    @Override
    public boolean updateOrderStatus(int orderId, OrderStatus newStatus) {
        Optional<Order> orderOpt = findById(orderId);
        if (orderOpt.isPresent()) {
            orderOpt.get().setOrderStatus(newStatus);
            return true;
        }
        return false;
    }

    /**
     * Concept: Atomic Post-Increment
     * Why: Generates a guaranteed unique order ID for each new transaction.
     */
    @Override
    public int getNextOrderId() {
        return orderIdSequence++;
    }
}
