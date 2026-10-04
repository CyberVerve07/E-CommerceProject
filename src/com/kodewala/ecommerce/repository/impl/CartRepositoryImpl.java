package com.kodewala.ecommerce.repository.impl;

import com.kodewala.ecommerce.model.CartItem;
import com.kodewala.ecommerce.repository.CartRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class Name: CartRepositoryImpl
 * Layer: Repository / Data Access Layer
 *
 * Description:
 * Implements in-memory data storage for Customer Carts.
 *
 * Concepts Used:
 * 1. Java Collections (Map<Integer, List<CartItem>>):
 *    - Uses a HashMap mapping each customer ID (Integer) to their personal list of CartItems (ArrayList).
 * 2. SOLID - SRP (Single Responsibility Principle):
 *    - Keeps shopping cart state separated from customer personal profile and product inventory.
 */
public class CartRepositoryImpl implements CartRepository {

    // Map storing customerId -> List of CartItems in their cart
    private final Map<Integer, List<CartItem>> customerCarts = new HashMap<>();

    /**
     * Concept: Map computeIfAbsent & Defensive Copy
     * Why: Ensures every customer always has an active cart list and returns a copy to prevent external mutation.
     */
    @Override
    public List<CartItem> getCartByCustomerId(int customerId) {
        List<CartItem> cart = customerCarts.computeIfAbsent(customerId, k -> new ArrayList<>());
        return new ArrayList<>(cart);
    }

    /**
     * Concept: List lookup and state update
     * Why: If the product is already in customer's cart, we merge quantities; otherwise, add new item.
     */
    @Override
    public void addItemToCart(int customerId, CartItem newItem) {
        List<CartItem> cart = customerCarts.computeIfAbsent(customerId, k -> new ArrayList<>());

        boolean found = false;
        for (CartItem existingItem : cart) {
            if (existingItem.getProductId() == newItem.getProductId()) {
                // Product already in cart, increment quantity
                existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
                found = true;
                break;
            }
        }

        if (!found) {
            cart.add(newItem);
        }
    }

    /**
     * Concept: Predicate Removal (removeIf)
     * Why: Efficiently removes matching cart item without manual index tracking or ConcurrentModificationException.
     */
    @Override
    public boolean removeItemFromCart(int customerId, int productId) {
        List<CartItem> cart = customerCarts.get(customerId);
        if (cart != null) {
            return cart.removeIf(item -> item.getProductId() == productId);
        }
        return false;
    }

    /**
     * Concept: Collection Clearing (List.clear)
     * Why: Cleans up customer's cart once checkout/order placement is successfully completed.
     */
    @Override
    public void clearCart(int customerId) {
        List<CartItem> cart = customerCarts.get(customerId);
        if (cart != null) {
            cart.clear();
        }
    }
}
