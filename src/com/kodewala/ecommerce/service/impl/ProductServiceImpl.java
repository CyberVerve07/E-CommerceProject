package com.kodewala.ecommerce.service.impl;

import com.kodewala.ecommerce.exception.ProductNotFoundException;
import com.kodewala.ecommerce.model.Product;
import com.kodewala.ecommerce.repository.ProductRepository;
import com.kodewala.ecommerce.service.ProductService;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class Name: ProductServiceImpl
 * Layer: Service / Business Logic Layer
 *
 * Description:
 * Implements business operations on Products: CRUD, searching, filtering, and sorting.
 *
 * Concepts Used:
 * 1. SOLID - Dependency Inversion Principle (DIP):
 *    - Injects ProductRepository interface via constructor. The service does not know
 *      or care whether the data is backed by an ArrayList, HashMap, or a real database.
 * 2. Java 8 Stream API:
 *    - Uses filter, sorted, and collect to perform declarative searching and filtering.
 * 3. Custom Exception Handling:
 *    - Throws ProductNotFoundException when an operation targets a nonexistent product ID.
 */
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    /**
     * Concept: Constructor Injection (DIP)
     * Why: Promotes loose coupling and simplifies unit testing with mocks/stubs.
     */
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Concept: Business Validation & Delegation
     * What: Validates product properties (price > 0, quantity >= 0) and saves to repository.
     * Why: Ensures corrupted data never enters the storage layer.
     */
    @Override
    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("Product price cannot be negative.");
        }
        if (product.getQuantity() < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative.");
        }
        productRepository.addProduct(product);
    }

    /**
     * Concept: Delegation
     * What: Fetches all products currently stored.
     */
    @Override
    public List<Product> getAllProducts() {
        return productRepository.getAllProducts();
    }

    /**
     * Concept: Custom Exception Handling & Java 8 Optional.orElseThrow
     * What: Finds product by ID or throws ProductNotFoundException.
     * Why: Idiomatic Java 8 pattern for handling missing resources.
     */
    @Override
    public Product getProductById(int productId) throws ProductNotFoundException {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " does not exist."));
    }

    /**
     * Concept: Java 8 Stream API (Filtering with case-insensitive matching)
     * What: Searches products whose names contain the given query string.
     * Why: Declarative Streams provide readable, concise filtering without manual for-loops.
     */
    @Override
    public List<Product> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllProducts();
        }
        String query = name.trim().toLowerCase();
        return productRepository.getAllProducts().stream()
                .filter(p -> p.getProductName() != null && p.getProductName().toLowerCase().contains(query))
                .collect(Collectors.toList());
    }

    /**
     * Concept: Java 8 Stream API (Filter by category)
     * What: Returns all products belonging to the specified category.
     * Why: Stream.filter checks category equality ignoring case differences.
     */
    @Override
    public List<Product> filterByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return getAllProducts();
        }
        String targetCategory = category.trim().toLowerCase();
        return productRepository.getAllProducts().stream()
                .filter(p -> p.getCategory() != null && p.getCategory().toLowerCase().equalsIgnoreCase(targetCategory))
                .collect(Collectors.toList());
    }

    /**
     * Concept: Java 8 Stream API (Filter by brand)
     * What: Returns all products from the specified brand.
     * Why: Stream.filter easily matches the brand attribute across the catalog.
     */
    @Override
    public List<Product> filterByBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            return getAllProducts();
        }
        String targetBrand = brand.trim().toLowerCase();
        return productRepository.getAllProducts().stream()
                .filter(p -> p.getBrand() != null && p.getBrand().toLowerCase().equalsIgnoreCase(targetBrand))
                .collect(Collectors.toList());
    }

    /**
     * Concept: Java 8 Stream API (Multi-condition Numerical Range Filter)
     * What: Returns products where price is between minPrice and maxPrice.
     * Why: Stream predicate combining (price >= min && price <= max) is clean and composable.
     */
    @Override
    public List<Product> filterByPriceRange(double minPrice, double maxPrice) {
        return productRepository.getAllProducts().stream()
                .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    /**
     * Concept: Java 8 Stream API (Sorting with Comparator)
     * What: Sorts the list of products by price in ascending or descending order.
     * Why: Comparator.comparingDouble(Product::getPrice) uses method references for concise sorting.
     */
    @Override
    public List<Product> sortByPrice(boolean ascending) {
        Comparator<Product> priceComparator = Comparator.comparingDouble(Product::getPrice);
        if (!ascending) {
            priceComparator = priceComparator.reversed();
        }
        return productRepository.getAllProducts().stream()
                .sorted(priceComparator)
                .collect(Collectors.toList());
    }

    /**
     * Concept: Input Validation & Exception Propagation
     * What: Updates price of an existing product; validates new price > 0.
     * Why: Business rule prevents setting negative or zero prices.
     */
    @Override
    public void updateProductPrice(int productId, double newPrice) throws ProductNotFoundException {
        if (newPrice <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero.");
        }
        boolean updated = productRepository.updatePrice(productId, newPrice);
        if (!updated) {
            throw new ProductNotFoundException("Product with ID " + productId + " does not exist.");
        }
    }

    /**
     * Concept: Input Validation & Exception Propagation
     * What: Updates stock quantity for a product.
     * Why: Stock quantity cannot be negative.
     */
    @Override
    public void updateProductQuantity(int productId, int newQuantity) throws ProductNotFoundException {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative.");
        }
        boolean updated = productRepository.updateQuantity(productId, newQuantity);
        if (!updated) {
            throw new ProductNotFoundException("Product with ID " + productId + " does not exist.");
        }
    }

    /**
     * Concept: Deletion with Existence Check
     * What: Removes a product by ID from the system.
     * Why: Raises ProductNotFoundException if attempting to delete a missing product.
     */
    @Override
    public void deleteProduct(int productId) throws ProductNotFoundException {
        boolean deleted = productRepository.deleteProduct(productId);
        if (!deleted) {
            throw new ProductNotFoundException("Product with ID " + productId + " does not exist.");
        }
    }
}
