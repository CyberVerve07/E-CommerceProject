package com.kodewala.ecommerce.service;

import com.kodewala.ecommerce.exception.InsufficientStockException;
import com.kodewala.ecommerce.exception.InvalidQuantityException;
import com.kodewala.ecommerce.exception.ProductNotFoundException;
import com.kodewala.ecommerce.model.CartItem;
import java.util.List;

/**
 * ============================================================================
 * SOLID Principles:
 * - ISP (Interface Segregation Principle): Focused interface for Cart operations.
 * - DIP (Dependency Inversion Principle): UI layers depend on this abstraction.
 * ============================================================================
 */
public interface CartService {

    // Adds a product to the customer's cart after checking stock
    void addToCart(int customerId, int productId, int quantity)
            throws ProductNotFoundException, InsufficientStockException, InvalidQuantityException;

    // Removes a product completely from the cart
    void removeFromCart(int customerId, int productId);

    // Increases the quantity of a product in the cart
    void increaseQuantity(int customerId, int productId, int quantityToAdd)
            throws InsufficientStockException, InvalidQuantityException;

    // Decreases the quantity of a product in the cart
    void decreaseQuantity(int customerId, int productId, int quantityToSubtract)
            throws InvalidQuantityException;

    // Retrieves all items in the customer's cart
    List<CartItem> getCartItems(int customerId);

    // Calculates the total cart value using Java 8 Stream API
    double calculateCartTotal(int customerId);

    // Clears all items in customer's cart
    void clearCart(int customerId);
}
