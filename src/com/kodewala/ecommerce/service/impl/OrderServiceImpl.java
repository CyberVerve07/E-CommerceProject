package com.kodewala.ecommerce.service.impl;

import com.kodewala.ecommerce.exception.InsufficientStockException;
import com.kodewala.ecommerce.exception.InvalidOrderException;
import com.kodewala.ecommerce.model.CartItem;
import com.kodewala.ecommerce.model.Order;
import com.kodewala.ecommerce.model.OrderStatus;
import com.kodewala.ecommerce.model.Product;
import com.kodewala.ecommerce.repository.CartRepository;
import com.kodewala.ecommerce.repository.OrderRepository;
import com.kodewala.ecommerce.repository.ProductRepository;
import com.kodewala.ecommerce.service.OrderService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Class Name: OrderServiceImpl
 * Layer: Service / Business Logic Layer
 *
 * Description:
 * Implements business logic for Order lifecycle:
 * - Order placement with inventory deduction and cart clearing.
 * - Order retrieval (details and customer order history).
 * - Order cancellation with inventory restoration.
 *
 * Concepts Used:
 * 1. SOLID - SRP (Single Responsibility Principle):
 *    - Manages transactional business logic for order processing.
 * 2. SOLID - DIP (Dependency Inversion Principle):
 *    - Injects OrderRepository, CartRepository, and ProductRepository abstractions.
 * 3. Atomic Order Flow & Stock Consistency:
 *    - Validates all cart items before deducting stock, ensuring consistency across inventory.
 * 4. Custom Exception Handling:
 *    - Throws InvalidOrderException or InsufficientStockException on failure.
 */
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    /**
     * Concept: Constructor Injection (DIP)
     * Why: OrderService coordinates between orders, carts, and product inventory.
     */
    public OrderServiceImpl(OrderRepository orderRepository,
                            CartRepository cartRepository,
                            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    /**
     * Concept: Transactional Checkout Workflow
     * Steps:
     * 1. Check if customer cart has items (else throw InvalidOrderException).
     * 2. Verify all items have sufficient inventory stock (else throw InsufficientStockException).
     * 3. Deduct product quantities from warehouse inventory (Inventory Management).
     * 4. Calculate total amount and generate unique Order ID.
     * 5. Save new Order with status PLACED.
     * 6. Clear customer's shopping cart.
     * Why: Enforces complete business rule lifecycle without leaving partial state.
     */
    @Override
    public Order placeOrder(int customerId) throws InvalidOrderException, InsufficientStockException {
        List<CartItem> cartItems = cartRepository.getCartByCustomerId(customerId);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new InvalidOrderException("Cannot place order: Your cart is empty!");
        }

        // Step 1: Pre-validate stock for all items BEFORE mutating any inventory
        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new InvalidOrderException("Product '" + item.getProductName() + "' (ID: " + item.getProductId() + ") is no longer available."));

            if (item.getQuantity() > product.getQuantity()) {
                throw new InsufficientStockException(String.format(
                        "Cannot place order! Product '%s' only has %d in stock, but your cart has %d.",
                        product.getProductName(), product.getQuantity(), item.getQuantity()));
            }
        }

        // Step 2: Deduct stock from inventory
        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProductId()).get();
            int remainingStock = product.getQuantity() - item.getQuantity();
            productRepository.updateQuantity(product.getProductId(), remainingStock);
        }

        // Step 3: Compute total amount using Stream API
        double totalAmount = cartItems.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();

        // Step 4: Create Order entity
        int newOrderId = orderRepository.getNextOrderId();
        Order order = new Order(newOrderId, customerId, cartItems, totalAmount, OrderStatus.PLACED, LocalDateTime.now());

        // Step 5: Save order
        orderRepository.saveOrder(order);

        // Step 6: Clear customer's cart
        cartRepository.clearCart(customerId);

        return order;
    }

    /**
     * Concept: Optional handling & Exception Throwing
     * What: Retrieves details for a specific order.
     */
    @Override
    public Order getOrderById(int orderId) throws InvalidOrderException {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new InvalidOrderException("Order with ID " + orderId + " does not exist."));
    }

    /**
     * Concept: Customer Order History
     * What: Returns all past and present orders placed by a specific customer.
     */
    @Override
    public List<Order> getOrdersByCustomerId(int customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    /**
     * Concept: Administrator View
     * What: Returns all orders placed across the entire store.
     */
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    /**
     * Concept: State Transition & Inventory Rollback
     * What: Cancels an order, sets status to CANCELLED, and restores product stock.
     * Why: If a customer cancels an active order, stock must be returned to inventory.
     */
    @Override
    public void cancelOrder(int orderId) throws InvalidOrderException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new InvalidOrderException("Order with ID " + orderId + " does not exist."));

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderException("Order #" + orderId + " is already CANCELLED.");
        }

        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new InvalidOrderException("Order #" + orderId + " has already been DELIVERED and cannot be cancelled.");
        }

        // Update status to CANCELLED
        orderRepository.updateOrderStatus(orderId, OrderStatus.CANCELLED);

        // Inventory Rollback: Restore quantities of all ordered items
        for (CartItem item : order.getItems()) {
            Optional<Product> productOpt = productRepository.findById(item.getProductId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                int restoredStock = product.getQuantity() + item.getQuantity();
                productRepository.updateQuantity(product.getProductId(), restoredStock);
            }
        }
    }
}
