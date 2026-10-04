package com.kodewala.ecommerce.service;

import com.kodewala.ecommerce.exception.CustomerNotFoundException;
import com.kodewala.ecommerce.model.Customer;
import java.util.List;

/**
 * ============================================================================
 * SOLID Principles:
 * - ISP (Interface Segregation Principle): Focused interface for Customer operations.
 * - DIP (Dependency Inversion Principle): UI layers depend on this abstraction.
 * ============================================================================
 */
public interface CustomerService {

    // Registers a new customer into the system
    void registerCustomer(Customer customer);

    // Retrieves customer details by ID; throws CustomerNotFoundException if not found
    Customer getCustomerById(int customerId) throws CustomerNotFoundException;

    // Updates customer address
    void updateAddress(int customerId, String newAddress) throws CustomerNotFoundException;

    // Retrieves all registered customers
    List<Customer> getAllCustomers();

    // Authenticates/validates customer login by ID
    Customer login(int customerId) throws CustomerNotFoundException;
}
