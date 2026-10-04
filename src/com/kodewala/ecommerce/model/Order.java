package com.kodewala.ecommerce.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class Name: Order
 * Layer: Model / Entity
 *
 * Description:
 * Represents a completed checkout order placed by a customer.
 * Contains a snapshot of purchased items, grand total, status, and timestamp.
 *
 * Concepts Used:
 * 1. Composition: An Order HAS-A collection of CartItem objects (List<CartItem>).
 * 2. Immutable/Safe Copy: Makes defensive copies of item lists to prevent external modification.
 * 3. Enums: Uses OrderStatus enum to restrict valid state transitions.
 */
public class Order {

    private int orderId;
    private int customerId;
    private List<CartItem> items;
    private double totalAmount;
    private OrderStatus orderStatus;
    private LocalDateTime orderDate;

    // Default No-Argument Constructor
    public Order() {
        this.items = new ArrayList<>();
        this.orderDate = LocalDateTime.now();
        this.orderStatus = OrderStatus.PLACED;
    }

    // Parameterized Constructor
    public Order(int orderId, int customerId, List<CartItem> items, double totalAmount, OrderStatus orderStatus, LocalDateTime orderDate) {
        this.orderId = orderId;
        this.customerId = customerId;
        // Defensive copy of items
        this.items = (items != null) ? new ArrayList<>(items) : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.orderDate = (orderDate != null) ? orderDate : LocalDateTime.now();
    }

    // --- Getter and Setter Methods ---

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = (items != null) ? new ArrayList<>(items) : new ArrayList<>();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return orderId == order.orderId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = (orderDate != null) ? orderDate.format(formatter) : "N/A";
        return String.format("Order ID: %-5d | Customer ID: %-4d | Date: %-16s | Amount: Rs. %-8.2f | Status: %-10s",
                orderId, customerId, formattedDate, totalAmount, orderStatus);
    }
}
