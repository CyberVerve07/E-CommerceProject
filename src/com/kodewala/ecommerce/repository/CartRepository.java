package com.kodewala.ecommerce.repository;

import com.kodewala.ecommerce.model.CartItem;
import java.util.List;

/**
 * ============================================================================
 * SOLID Principles:
 * - ISP (Interface Segregation Principle): Dedicated interface for Cart persistence.
 * - Suggested storage: Map<Integer, List<CartItem>> (customerId -> list of cart items).
 * ============================================================================
 */
public interface CartRepository {

    // Retrieves the current cart items for a given customer
    List<CartItem> getCartByCustomerId(int customerId);

    // Adds a CartItem to the customer's cart
    void addItemToCart(int customerId, CartItem item);

    // Removes a CartItem from the customer's cart by productId
    boolean removeItemFromCart(int customerId, int productId);

    // Clears all items in the customer's cart (e.g., after successful checkout)
    void clearCart(int customerId);
}
