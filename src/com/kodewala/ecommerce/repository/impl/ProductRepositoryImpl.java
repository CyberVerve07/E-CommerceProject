package com.kodewala.ecommerce.repository.impl;

import com.kodewala.ecommerce.model.Product;
import com.kodewala.ecommerce.repository.ProductRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Class Name: ProductRepositoryImpl
 * Layer: Repository / Data Access Layer
 *
 * Description:
 * Implements in-memory data storage for Products using Java Collections.
 * All CRUD operations on products are performed here.
 *
 * Concepts Used:
 * 1. Java Collections (ArrayList & HashSet):
 *    - Uses ArrayList<Product> to maintain products in an ordered list as required by the specification.
 *    - Uses HashSet<String> to track unique categories, demonstrating HashSet collection practice.
 * 2. Java 8 Optional:
 *    - Returned by findById() to prevent NullPointerException and force callers to handle missing items.
 * 3. SOLID - Single Responsibility Principle (SRP):
 *    - Dedicated solely to data access and mutation; contains zero console I/O.
 * 4. SOLID - Liskov Substitution Principle (LSP):
 *    - Fully honors the contract defined by ProductRepository interface.
 */
public class ProductRepositoryImpl implements ProductRepository {

    // Main collection to store products (ArrayList satisfies requirement for List<Product>)
    private final List<Product> productList = new ArrayList<>();

    // Demonstrates HashSet usage to maintain unique categories without duplicates
    private final Set<String> categories = new HashSet<>();

    /**
     * Constructor: Initializes repository with sample initial products
     * so that the application has ready-to-test data out of the box.
     */
    public ProductRepositoryImpl() {
        seedInitialProducts();
    }

    private void seedInitialProducts() {
        addProduct(new Product(101, "iPhone 16", "Electronics", 79999.00, 10, "Apple"));
        addProduct(new Product(102, "Galaxy S24", "Electronics", 74999.00, 15, "Samsung"));
        addProduct(new Product(103, "MacBook Air M3", "Computers", 114900.00, 8, "Apple"));
        addProduct(new Product(104, "Sony WH-1000XM5", "Audio", 26990.00, 20, "Sony"));
        addProduct(new Product(105, "Dell XPS 15", "Computers", 145000.00, 5, "Dell"));
        addProduct(new Product(106, "AirPods Pro 2", "Audio", 24900.00, 12, "Apple"));
    }

    /**
     * Concept: Collection Insertion (ArrayList.add) & Set Insertion (HashSet.add)
     * Why: Adds product to list and updates the unique categories set.
     */
    @Override
    public void addProduct(Product product) {
        if (product != null) {
            productList.add(product);
            if (product.getCategory() != null) {
                categories.add(product.getCategory().toLowerCase());
            }
        }
    }

    /**
     * Concept: Defensive Copy (Collections.unmodifiableList or new ArrayList)
     * Why: Returning a copy prevents callers from directly tampering with internal list.
     */
    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(productList);
    }

    /**
     * Concept: Java 8 Stream API & Optional
     * Why: Searches product list cleanly and returns an Optional to safely handle absent products.
     */
    @Override
    public Optional<Product> findById(int productId) {
        return productList.stream()
                .filter(p -> p.getProductId() == productId)
                .findFirst();
    }

    /**
     * Concept: State Mutation
     * Why: Finds product by ID and modifies its price attribute.
     */
    @Override
    public boolean updatePrice(int productId, double newPrice) {
        Optional<Product> productOpt = findById(productId);
        if (productOpt.isPresent()) {
            productOpt.get().setPrice(newPrice);
            return true;
        }
        return false;
    }

    /**
     * Concept: State Mutation
     * Why: Finds product by ID and modifies available stock quantity.
     */
    @Override
    public boolean updateQuantity(int productId, int newQuantity) {
        Optional<Product> productOpt = findById(productId);
        if (productOpt.isPresent()) {
            productOpt.get().setQuantity(newQuantity);
            return true;
        }
        return false;
    }

    /**
     * Concept: List Removal using Stream/Predicate (removeIf)
     * Why: removeIf is an atomic, concise method introduced in Java 8 to remove elements matching a predicate.
     */
    @Override
    public boolean deleteProduct(int productId) {
        return productList.removeIf(p -> p.getProductId() == productId);
    }

    /**
     * Concept: Predicate Check using anyMatch
     * Why: Checks presence without needing to retrieve the full object.
     */
    @Override
    public boolean existsById(int productId) {
        return productList.stream().anyMatch(p -> p.getProductId() == productId);
    }
}
