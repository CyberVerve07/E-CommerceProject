package com.kodewala.ecommerce.model;

import java.util.Objects;

/**
 * Class Name: Product
 * Layer: Model / Entity
 *
 * Description:
 * Represents a physical product available in the store.
 * Encapsulates all product details: ID, name, category, price, stock quantity, and brand.
 *
 * Concepts Used:
 * 1. Encapsulation: All attributes are marked 'private' and accessed via public getters and setters.
 *    This protects internal data from invalid direct modifications.
 * 2. SOLID - Single Responsibility Principle (SRP): This class only holds product data
 *    and does not contain business logic or storage handling.
 */
public class Product {

    private int productId;
    private String productName;
    private String category;
    private double price;
    private int quantity; // Available stock in inventory
    private String brand;

    // Default No-Argument Constructor
    public Product() {
    }

    // Parameterized Constructor to initialize all product fields
    public Product(int productId, String productName, String category, double price, int quantity, String brand) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.brand = brand;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Concept: equals & hashCode Override
     * Why: Allows products to be compared by their unique productId when stored in Collections
     * (e.g., HashSet or when using List.contains).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return productId == product.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    /**
     * Concept: toString Override
     * Why: Provides a clean tabular representation for console display.
     */
    @Override
    public String toString() {
        return String.format("ID: %-4d | Name: %-18s | Category: %-12s | Brand: %-10s | Price: Rs. %-8.2f | Stock: %-4d",
                productId, productName, category, brand, price, quantity);
    }
}
