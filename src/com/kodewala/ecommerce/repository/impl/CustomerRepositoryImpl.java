package com.kodewala.ecommerce.repository.impl;

import com.kodewala.ecommerce.model.Customer;
import com.kodewala.ecommerce.repository.CustomerRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Class Name: CustomerRepositoryImpl
 * Layer: Repository / Data Access Layer
 *
 * Description:
 * Implements in-memory data storage for Customers using Java Collections.
 *
 * Concepts Used:
 * 1. Java Collections (HashMap & HashSet):
 *    - Uses Map<Integer, Customer> (HashMap) for O(1) constant-time customer lookup by ID.
 *    - Uses Set<String> (HashSet) for registered emails to enforce unique email validation.
 * 2. Java 8 Optional:
 *    - Returns Optional<Customer> for findById to safely encapsulate nullable lookups.
 * 3. SOLID - SRP (Single Responsibility Principle):
 *    - Solely handles persistence operations for Customer records.
 */
public class CustomerRepositoryImpl implements CustomerRepository {

    // Map storing customerId -> Customer object (HashMap fulfills Map<Integer, Customer> requirement)
    private final Map<Integer, Customer> customerMap = new HashMap<>();

    // Set storing registered emails (HashSet enforces uniqueness)
    private final Set<String> registeredEmails = new HashSet<>();

    /**
     * Constructor: Pre-seeds a default test customer for quick verification.
     */
    public CustomerRepositoryImpl() {
        seedInitialCustomers();
    }

    private void seedInitialCustomers() {
        Customer defaultCustomer = new Customer(1, "Rahul Sharma", "rahul@kodewala.com", "9876543210", "123 MG Road, Bangalore");
        registerCustomer(defaultCustomer);
    }

    /**
     * Concept: Map Insertion (put) & Set Insertion (add)
     * Why: Puts customer into HashMap by ID and stores normalized email into HashSet.
     */
    @Override
    public void registerCustomer(Customer customer) {
        if (customer != null) {
            customerMap.put(customer.getCustomerId(), customer);
            if (customer.getEmail() != null) {
                registeredEmails.add(customer.getEmail().toLowerCase());
            }
        }
    }

    /**
     * Concept: Map Lookup with Optional
     * Why: map.get(id) returns null if missing; Optional.ofNullable handles this gracefully.
     */
    @Override
    public Optional<Customer> findById(int customerId) {
        return Optional.ofNullable(customerMap.get(customerId));
    }

    /**
     * Concept: Collection Conversion
     * Why: customerMap.values() returns a Collection view; copying into ArrayList creates an isolated snapshot.
     */
    @Override
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customerMap.values());
    }

    /**
     * Concept: In-place Map Element Update
     * Why: Looks up existing entity and mutates its address field.
     */
    @Override
    public boolean updateAddress(int customerId, String newAddress) {
        Customer customer = customerMap.get(customerId);
        if (customer != null) {
            customer.setAddress(newAddress);
            return true;
        }
        return false;
    }

    /**
     * Concept: Map Key Search (containsKey)
     * Why: Runs in O(1) time complexity compared to iterating over a list.
     */
    @Override
    public boolean existsById(int customerId) {
        return customerMap.containsKey(customerId);
    }

    /**
     * Concept: Set Membership Check (contains)
     * Why: HashSet.contains runs in O(1) time complexity, ensuring email uniqueness check is very fast.
     */
    @Override
    public boolean existsByEmail(String email) {
        if (email == null) return false;
        return registeredEmails.contains(email.toLowerCase());
    }
}
