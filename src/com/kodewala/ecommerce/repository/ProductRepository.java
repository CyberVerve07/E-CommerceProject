package com.kodewala.ecommerce.repository;

import com.kodewala.ecommerce.model.Product;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * SOLID Principles:
 * - ISP (Interface Segregation Principle): Dedicated interface for Product persistence.
 * - DIP (Dependency Inversion Principle): Services depend on this interface, not the concrete collection storage.
 * ============================================================================
 */
public interface ProductRepository {

    // Adds a new product to the collection
    void addProduct(Product product);

    // Retrieves all products
    List<Product> getAllProducts();

    // Finds a product by its ID
    Optional<Product> findById(int productId);

    // Updates product price
    boolean updatePrice(int productId, double newPrice);

    // Updates product quantity/stock
    boolean updateQuantity(int productId, int newQuantity);

    // Deletes a product by ID
    boolean deleteProduct(int productId);

    // Checks if a product exists by ID
    boolean existsById(int productId);
}
