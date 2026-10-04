package com.kodewala.ecommerce.service.impl;

import com.kodewala.ecommerce.exception.InsufficientStockException;
import com.kodewala.ecommerce.exception.InvalidQuantityException;
import com.kodewala.ecommerce.exception.ProductNotFoundException;
import com.kodewala.ecommerce.model.CartItem;
import com.kodewala.ecommerce.model.Product;
import com.kodewala.ecommerce.repository.CartRepository;
import com.kodewala.ecommerce.repository.ProductRepository;
import com.kodewala.ecommerce.service.CartService;
import java.util.List;

/**
 * Class Name: CartServiceImpl
 * Layer: Service / Business Logic Layer
 *
 * Description:
 * Implements shopping cart business logic: adding products, adjusting quantities,
 * stock limit validation, and calculating cart total using Streams.
 *
 * Concepts Used:
 * 1. SOLID - Dependency Inversion Principle (DIP):
 *    - Injects CartRepository and ProductRepository interfaces via constructor.
 * 2. Java 8 Stream API:
 *    - Calculates the grand total using mapToDouble() and sum().
 * 3. Business Exception Handling:
 *    - Throws InvalidQuantityException, InsufficientStockException, or ProductNotFoundException.
 */
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    /**
     * Concept: Constructor Injection (DIP)
     * Why: Coordinates between cart storage and product catalog to verify stock and prices.
     */
    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    /**
     * Concept: Multi-step Business Validation
     * What: Checks quantity validity, product existence, and stock availability before adding to cart.
     * Why: Prevents ordering non-existent items or quantities exceeding warehouse stock.
     */
    @Override
    public void addToCart(int customerId, int productId, int quantity)
            throws ProductNotFoundException, InsufficientStockException, InvalidQuantityException {

        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity to add must be greater than zero.");
        }

        // Verify product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " does not exist."));

        // Check if customer already has this item in cart to calculate total demanded quantity
        List<CartItem> currentCart = cartRepository.getCartByCustomerId(customerId);
        int existingCartQty = currentCart.stream()
                .filter(item -> item.getProductId() == productId)
                .mapToInt(CartItem::getQuantity)
                .sum();

        int totalRequestedQty = existingCartQty + quantity;

        if (totalRequestedQty > product.getQuantity()) {
            throw new InsufficientStockException(String.format(
                    "Insufficient stock! Available: %d, Currently in cart: %d, Requested additional: %d.",
                    product.getQuantity(), existingCartQty, quantity));
        }

        // Create CartItem snapshot and save
        CartItem item = new CartItem(product.getProductId(), product.getProductName(), product.getPrice(), quantity);
        cartRepository.addItemToCart(customerId, item);
    }

    /**
     * Concept: Delegation
     * What: Removes an item from the customer's cart.
     */
    @Override
    public void removeFromCart(int customerId, int productId) {
        cartRepository.removeItemFromCart(customerId, productId);
    }

    /**
     * Concept: Stock Validation on Quantity Increase
     * What: Increments cart quantity if additional stock is available in product inventory.
     */
    @Override
    public void increaseQuantity(int customerId, int productId, int quantityToAdd)
            throws InsufficientStockException, InvalidQuantityException {

        if (quantityToAdd <= 0) {
            throw new InvalidQuantityException("Quantity to add must be greater than zero.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " does not exist."));

        List<CartItem> currentCart = cartRepository.getCartByCustomerId(customerId);
        CartItem cartItem = currentCart.stream()
                .filter(item -> item.getProductId() == productId)
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Product not found in your cart."));

        int newTotalQty = cartItem.getQuantity() + quantityToAdd;
        if (newTotalQty > product.getQuantity()) {
            throw new InsufficientStockException(String.format(
                    "Cannot increase quantity! Available stock is %d, requested total is %d.",
                    product.getQuantity(), newTotalQty));
        }

        // Add increment
        cartRepository.addItemToCart(customerId, new CartItem(productId, product.getProductName(), product.getPrice(), quantityToAdd));
    }

    /**
     * Concept: Quantity Adjustment / Item Removal
     * What: Decreases item quantity in the cart; if quantity drops to 0 or below, removes the item.
     */
    @Override
    public void decreaseQuantity(int customerId, int productId, int quantityToSubtract)
            throws InvalidQuantityException {

        if (quantityToSubtract <= 0) {
            throw new InvalidQuantityException("Quantity to subtract must be greater than zero.");
        }

        List<CartItem> currentCart = cartRepository.getCartByCustomerId(customerId);
        CartItem cartItem = currentCart.stream()
                .filter(item -> item.getProductId() == productId)
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Product not found in your cart."));

        int remainingQty = cartItem.getQuantity() - quantityToSubtract;
        if (remainingQty <= 0) {
            // Remove completely from cart
            cartRepository.removeItemFromCart(customerId, productId);
        } else {
            // Reset cart with updated quantity
            cartRepository.removeItemFromCart(customerId, productId);
            CartItem updatedItem = new CartItem(productId, cartItem.getProductName(), cartItem.getPrice(), remainingQty);
            cartRepository.addItemToCart(customerId, updatedItem);
        }
    }

    /**
     * Concept: Delegation
     * What: Returns current cart items for the given customer.
     */
    @Override
    public List<CartItem> getCartItems(int customerId) {
        return cartRepository.getCartByCustomerId(customerId);
    }

    /**
     * Concept: Java 8 Stream API (mapToDouble and sum)
     * What: Calculates total monetary value of all items currently in cart.
     * Why: mapToDouble creates a DoubleStream avoiding autoboxing overhead and calculates sum declaratively.
     */
    @Override
    public double calculateCartTotal(int customerId) {
        List<CartItem> items = cartRepository.getCartByCustomerId(customerId);
        return items.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    /**
     * Concept: Cart Cleanup
     * What: Wipes all items from the customer's cart.
     */
    @Override
    public void clearCart(int customerId) {
        cartRepository.clearCart(customerId);
    }
}
