package com.kodewala.ecommerce.model;

import java.util.Objects;

/**
 * Class Name: Customer
 * Layer: Model / Entity
 *
 * Description:
 * Represents a registered user/customer in the system.
 * Contains user profile data including contact details and delivery address.
 *
 * Concepts Used:
 * 1. Encapsulation: Attributes are private to protect customer identity and data integrity.
 * 2. SOLID - SRP (Single Responsibility Principle): Only manages customer profile state.
 */
public class Customer {

    private int customerId;
    private String name;
    private String email;
    private String mobile;
    private String address;

    // Default No-Argument Constructor
    public Customer() {
    }

    // Parameterized Constructor
    public Customer(int customerId, String name, String email, String mobile, String address) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
    }

    // --- Getter and Setter Methods ---

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return customerId == customer.customerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }

    @Override
    public String toString() {
        return String.format("Customer ID: %-4d | Name: %-16s | Email: %-22s | Phone: %-12s | Address: %s",
                customerId, name, email, mobile, address);
    }
}
