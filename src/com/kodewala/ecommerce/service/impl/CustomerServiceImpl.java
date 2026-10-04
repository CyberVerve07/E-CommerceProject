package com.kodewala.ecommerce.service.impl;

import com.kodewala.ecommerce.exception.CustomerNotFoundException;
import com.kodewala.ecommerce.model.Customer;
import com.kodewala.ecommerce.repository.CustomerRepository;
import com.kodewala.ecommerce.service.CustomerService;
import java.util.List;

/**
 * Class Name: CustomerServiceImpl
 * Layer: Service / Business Logic Layer
 *
 * Description:
 * Implements business operations for Customer registration, lookup, address updates, and authentication.
 *
 * Concepts Used:
 * 1. SOLID - DIP (Dependency Inversion Principle):
 *    - Injects CustomerRepository abstraction via constructor.
 * 2. Business Validation:
 *    - Validates email uniqueness and required fields before registering.
 * 3. Custom Exception Handling:
 *    - Throws CustomerNotFoundException when an unknown customer ID is accessed.
 */
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Concept: Constructor Injection (DIP)
     * Why: Enables swapping data implementations and easier unit testing.
     */
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Concept: Business Validation & Uniqueness Check
     * What: Ensures customer email and ID are unique and valid before storing.
     * Why: Preserves data integrity and prevents duplicate customer accounts.
     */
    @Override
    public void registerCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer details cannot be null.");
        }
        if (customerRepository.existsById(customer.getCustomerId())) {
            throw new IllegalArgumentException("Customer with ID " + customer.getCustomerId() + " already exists.");
        }
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Customer with email '" + customer.getEmail() + "' is already registered.");
        }
        customerRepository.registerCustomer(customer);
    }

    /**
     * Concept: Optional unwrapping & Custom Exception
     * What: Retrieves customer by ID; throws CustomerNotFoundException if absent.
     * Why: Provides descriptive, application-specific error feedback.
     */
    @Override
    public Customer getCustomerById(int customerId) throws CustomerNotFoundException {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + customerId + " not found."));
    }

    /**
     * Concept: Field Update with Exception Handling
     * What: Updates delivery address of an existing customer.
     * Why: Address cannot be empty and customer must exist.
     */
    @Override
    public void updateAddress(int customerId, String newAddress) throws CustomerNotFoundException {
        if (newAddress == null || newAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty.");
        }
        boolean updated = customerRepository.updateAddress(customerId, newAddress.trim());
        if (!updated) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found.");
        }
    }

    /**
     * Concept: Delegation
     * What: Returns all registered customers for the administrator view.
     */
    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.getAllCustomers();
    }

    /**
     * Concept: Authentication via ID Lookup
     * What: Verifies customer existence to initiate a session.
     * Why: In this console project without passwords, customer ID serves as the login credential.
     */
    @Override
    public Customer login(int customerId) throws CustomerNotFoundException {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Login failed: Customer ID " + customerId + " is not registered."));
    }
}
