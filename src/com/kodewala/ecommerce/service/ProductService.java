package com.kodewala.ecommerce.service;

import com.kodewala.ecommerce.exception.ProductNotFoundException;
import com.kodewala.ecommerce.model.Product;
import java.util.List;

/**
 * ============================================================================
 * SOLID Principles:
 * - ISP (Interface Segregation Principle): Focused interface for Product business logic.
 * - DIP (Dependency Inversion Principle): UI layers depend on this interface.
 * ============================================================================
 */
public interface ProductService {

    // Adds a new product to the system
    void addProduct(Product product);

    // Retrieves all products
    List<Product> getAllProducts();

    // Searches product by unique ID; throws ProductNotFoundException if not found
    Product getProductById(int productId) throws ProductNotFoundException;

    // Searches products by name (case-insensitive, Java 8 Stream API)
    List<Product> searchByName(String name);

    // Filters products by category (Java 8 Stream API)
    List<Product> filterByCategory(String category);

    // Filters products by brand (Java 8 Stream API)
    List<Product> filterByBrand(String brand);

    // Filters products within price range [minPrice, maxPrice] (Java 8 Stream API)
    List<Product> filterByPriceRange(double minPrice, double maxPrice);

    // Sorts products by price (ascending/descending using Java 8 Stream API)
    List<Product> sortByPrice(boolean ascending);

    // Updates product price
    void updateProductPrice(int productId, double newPrice) throws ProductNotFoundException;

    // Updates product stock quantity
    void updateProductQuantity(int productId, int newQuantity) throws ProductNotFoundException;

    // Deletes product from system
    void deleteProduct(int productId) throws ProductNotFoundException;
}
