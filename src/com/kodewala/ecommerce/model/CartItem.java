package com.kodewala.ecommerce.model;

import java.util.Objects;

/**
 * Class Name: CartItem
 * Layer: Model / Entity
 *
 * Description:
 * Represents a specific product line item within a customer's shopping cart.
 * Stores selected product reference, unit price, requested quantity, and calculated total price.
 *
 * Concepts Used:
 * 1. Encapsulation: Ensures that total price is automatically recalculated whenever quantity changes.
 * 2. SOLID - SRP (Single Responsibility Principle): Only manages cart item state.
 */
public class CartItem {

    private int productId;
    private String productName;
    private double price;
    private int quantity;
    private double totalPrice;

    // Default No-Argument Constructor
    public CartItem() {
    }

    // Parameterized Constructor
    public CartItem(int productId, String productName, double price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = price * quantity;
    }

    // --- Getter and Setter Methods ---

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        this.totalPrice = this.price * this.quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Updates item quantity and automatically recalculates totalPrice.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.totalPrice = this.price * this.quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem cartItem)) return false;
        return productId == cartItem.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return String.format("Product ID: %-4d | Item: %-18s | Price: Rs. %-8.2f | Qty: %-3d | Total: Rs. %-8.2f",
                productId, productName, price, quantity, totalPrice);
    }
}
