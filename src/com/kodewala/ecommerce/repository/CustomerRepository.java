package com.kodewala.ecommerce.repository;

import com.kodewala.ecommerce.model.Customer;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * SOLID Principles:
 * - ISP (Interface Segregation Principle): Dedicated interface for Customer persistence.
 * - DIP (Dependency Inversion Principle): Services depend on this abstraction.
 * ============================================================================
 */
public interface CustomerRepository {

    // Registers/saves a customer into the collection (e.g. Map<Integer, Customer>)
    void registerCustomer(Customer customer);

    // Retrieves a customer by their unique ID
    Optional<Customer> findById(int customerId);

    // Retrieves all registered customers
    List<Customer> getAllCustomers();

    // Updates customer address
    boolean updateAddress(int customerId, String newAddress);

    // Checks if a customer exists with the given ID
    boolean existsById(int customerId);

    // Checks if a customer exists with the given email
    boolean existsByEmail(String email);
}
