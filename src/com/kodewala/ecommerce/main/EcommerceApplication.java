package com.kodewala.ecommerce.main;

import com.kodewala.ecommerce.exception.CustomerNotFoundException;
import com.kodewala.ecommerce.exception.InsufficientStockException;
import com.kodewala.ecommerce.exception.InvalidOrderException;
import com.kodewala.ecommerce.exception.InvalidQuantityException;
import com.kodewala.ecommerce.exception.ProductNotFoundException;
import com.kodewala.ecommerce.model.CartItem;
import com.kodewala.ecommerce.model.Customer;
import com.kodewala.ecommerce.model.Order;
import com.kodewala.ecommerce.model.Product;
import com.kodewala.ecommerce.repository.CartRepository;
import com.kodewala.ecommerce.repository.CustomerRepository;
import com.kodewala.ecommerce.repository.OrderRepository;
import com.kodewala.ecommerce.repository.ProductRepository;
import com.kodewala.ecommerce.repository.impl.CartRepositoryImpl;
import com.kodewala.ecommerce.repository.impl.CustomerRepositoryImpl;
import com.kodewala.ecommerce.repository.impl.OrderRepositoryImpl;
import com.kodewala.ecommerce.repository.impl.ProductRepositoryImpl;
import com.kodewala.ecommerce.service.CartService;
import com.kodewala.ecommerce.service.CustomerService;
import com.kodewala.ecommerce.service.OrderService;
import com.kodewala.ecommerce.service.ProductService;
import com.kodewala.ecommerce.service.impl.CartServiceImpl;
import com.kodewala.ecommerce.service.impl.CustomerServiceImpl;
import com.kodewala.ecommerce.service.impl.OrderServiceImpl;
import com.kodewala.ecommerce.service.impl.ProductServiceImpl;

import java.util.List;
import java.util.Scanner;

/**
 * Class Name: EcommerceApplication
 * Layer: Presentation / Main Entry Point
 *
 * Description:
 * Interactive console interface for the E-Commerce Product & Order Management System.
 * Coordinates user input/output and delegates business operations to services.
 *
 * Concepts Used:
 * 1. SOLID - Single Responsibility Principle (SRP):
 *    - Main class solely manages console interactions and user menus; zero business calculations.
 * 2. SOLID - Dependency Inversion Principle (DIP):
 *    - Wires repositories into services via constructor injection.
 * 3. Exception Handling (try-catch):
 *    - Catches custom exceptions and displays clear, user-friendly messages without crashing.
 * 4. Scanner & Input Validation:
 *    - Safely consumes console inputs and handles invalid menu numbers.
 */
public class EcommerceApplication {

    private static final Scanner scanner = new Scanner(System.in);

    // Service layers injected with their respective repositories
    private static ProductService productService;
    private static CustomerService customerService;
    private static CartService cartService;
    private static OrderService orderService;

    // Currently logged-in customer session (null if guest)
    private static Customer currentCustomer = null;

    public static void main(String[] args) {
        // Step 1: Initialize Repositories (Data Storage Layer)
        ProductRepository productRepo = new ProductRepositoryImpl();
        CustomerRepository customerRepo = new CustomerRepositoryImpl();
        CartRepository cartRepo = new CartRepositoryImpl();
        OrderRepository orderRepo = new OrderRepositoryImpl();

        // Step 2: Initialize Services and inject Repositories (DIP Principle)
        productService = new ProductServiceImpl(productRepo);
        customerService = new CustomerServiceImpl(customerRepo);
        cartService = new CartServiceImpl(cartRepo, productRepo);
        orderService = new OrderServiceImpl(orderRepo, cartRepo, productRepo);

        // Step 3: Run the Main Navigation Loop
        boolean running = true;
        while (running) {
            printHeader("MAIN MENU");
            System.out.println("1. Admin");
            System.out.println("2. Customer");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = readIntChoice();
            switch (choice) {
                case 1 -> runAdminMenu();
                case 2 -> runCustomerMenu();
                case 3 -> {
                    System.out.println("\nThank you for using the E-Commerce Application. Goodbye!");
                    running = false;
                }
                default -> System.out.println("\n[!] Invalid choice. Please select 1, 2, or 3.");
            }
        }
    }

    // ========================================================================
    //                         ADMINISTRATOR SECTION
    // ========================================================================

    /**
     * Admin Menu: Handles catalog management, stock updates, customers, and all orders.
     */
    private static void runAdminMenu() {
        boolean inAdmin = true;
        while (inAdmin) {
            printHeader("ADMIN MENU");
            System.out.println("1. Add Product");
            System.out.println("2. View Products");
            System.out.println("3. Search Product");
            System.out.println("4. Update Product");
            System.out.println("5. Delete Product");
            System.out.println("6. View Customers");
            System.out.println("7. View All Orders");
            System.out.println("8. Exit to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = readIntChoice();
            switch (choice) {
                case 1 -> handleAddProduct();
                case 2 -> handleViewAllProducts();
                case 3 -> handleAdminSearchProduct();
                case 4 -> handleUpdateProduct();
                case 5 -> handleDeleteProduct();
                case 6 -> handleViewCustomers();
                case 7 -> handleViewAllOrders();
                case 8 -> inAdmin = false;
                default -> System.out.println("\n[!] Invalid choice. Please enter a number between 1 and 8.");
            }
        }
    }

    private static void handleAddProduct() {
        printHeader("ADD NEW PRODUCT");
        try {
            System.out.print("Enter Product ID: ");
            int id = readIntChoice();
            System.out.print("Enter Product Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Category: ");
            String category = scanner.nextLine().trim();
            System.out.print("Enter Brand: ");
            String brand = scanner.nextLine().trim();
            System.out.print("Enter Price (Rs.): ");
            double price = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Enter Initial Stock Quantity: ");
            int quantity = readIntChoice();

            Product product = new Product(id, name, category, price, quantity, brand);
            productService.addProduct(product);
            System.out.println("\n[SUCCESS] Product added successfully!");
        } catch (NumberFormatException e) {
            System.out.println("\n[!] Error: Invalid numeric input format.");
        } catch (Exception e) {
            System.out.println("\n[!] Error adding product: " + e.getMessage());
        }
    }

    private static void handleViewAllProducts() {
        printHeader("ALL PRODUCTS IN STORE");
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products available in the store.");
        } else {
            products.forEach(System.out::println);
        }
    }

    private static void handleAdminSearchProduct() {
        printHeader("SEARCH PRODUCT");
        System.out.println("1. Search by Product ID");
        System.out.println("2. Search by Product Name");
        System.out.println("3. Search by Category");
        System.out.print("Enter your choice: ");
        int choice = readIntChoice();

        try {
            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Product ID: ");
                    int id = readIntChoice();
                    Product p = productService.getProductById(id);
                    System.out.println("\nFound Product:\n" + p);
                }
                case 2 -> {
                    System.out.print("Enter Product Name: ");
                    String name = scanner.nextLine().trim();
                    List<Product> list = productService.searchByName(name);
                    displayProductList(list);
                }
                case 3 -> {
                    System.out.print("Enter Category: ");
                    String cat = scanner.nextLine().trim();
                    List<Product> list = productService.filterByCategory(cat);
                    displayProductList(list);
                }
                default -> System.out.println("[!] Invalid search option.");
            }
        } catch (ProductNotFoundException e) {
            System.out.println("\n[!] " + e.getMessage());
        }
    }

    private static void handleUpdateProduct() {
        printHeader("UPDATE PRODUCT");
        System.out.print("Enter Product ID to update: ");
        int id = readIntChoice();

        try {
            // Verify product exists first
            Product p = productService.getProductById(id);
            System.out.println("Current details: " + p);

            System.out.println("1. Update Price");
            System.out.println("2. Update Quantity (Stock)");
            System.out.print("Enter your choice: ");
            int option = readIntChoice();

            if (option == 1) {
                System.out.print("Enter new Price (Rs.): ");
                double newPrice = Double.parseDouble(scanner.nextLine().trim());
                productService.updateProductPrice(id, newPrice);
                System.out.println("[SUCCESS] Product price updated successfully!");
            } else if (option == 2) {
                System.out.print("Enter new Quantity: ");
                int newQty = readIntChoice();
                productService.updateProductQuantity(id, newQty);
                System.out.println("[SUCCESS] Product quantity updated successfully!");
            } else {
                System.out.println("[!] Invalid option.");
            }
        } catch (ProductNotFoundException | IllegalArgumentException e) {
            System.out.println("\n[!] " + e.getMessage());
        }
    }

    private static void handleDeleteProduct() {
        printHeader("DELETE PRODUCT");
        System.out.print("Enter Product ID to delete: ");
        int id = readIntChoice();

        try {
            productService.deleteProduct(id);
            System.out.println("\n[SUCCESS] Product deleted successfully!");
        } catch (ProductNotFoundException e) {
            System.out.println("\n[!] " + e.getMessage());
        }
    }

    private static void handleViewCustomers() {
        printHeader("REGISTERED CUSTOMERS");
        List<Customer> customers = customerService.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("No registered customers found.");
        } else {
            customers.forEach(System.out::println);
        }
    }

    private static void handleViewAllOrders() {
        printHeader("ALL STORE ORDERS (ADMIN)");
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders have been placed yet.");
        } else {
            for (Order o : orders) {
                System.out.println(o);
                System.out.println("   Ordered Items:");
                o.getItems().forEach(item -> System.out.println("     - " + item));
                System.out.println("   ---------------------------------------------");
            }
        }
    }

    // ========================================================================
    //                         CUSTOMER SECTION
    // ========================================================================

    /**
     * Customer Menu: Directs customer flows (Register, Login, Shopping, Cart, Orders).
     */
    private static void runCustomerMenu() {
        boolean inCustomer = true;
        while (inCustomer) {
            printHeader("CUSTOMER MENU" + (currentCustomer != null ? " [Logged in as: " + currentCustomer.getName() + "]" : " [Not Logged In]"));
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. View Products");
            System.out.println("4. Search Product");
            System.out.println("5. Add Product to Cart");
            System.out.println("6. View Cart");
            System.out.println("7. Remove Product from Cart");
            System.out.println("8. Place Order");
            System.out.println("9. View My Orders");
            System.out.println("10. Cancel Order");
            System.out.println("11. Logout / Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = readIntChoice();
            switch (choice) {
                case 1 -> handleCustomerRegister();
                case 2 -> handleCustomerLogin();
                case 3 -> handleViewAllProducts();
                case 4 -> handleCustomerSearchProduct();
                case 5 -> handleAddToCart();
                case 6 -> handleViewCart();
                case 7 -> handleRemoveFromCart();
                case 8 -> handlePlaceOrder();
                case 9 -> handleViewMyOrders();
                case 10 -> handleCancelOrder();
                case 11 -> {
                    currentCustomer = null;
                    inCustomer = false;
                }
                default -> System.out.println("\n[!] Invalid choice. Please enter a number between 1 and 11.");
            }
        }
    }

    private static void handleCustomerRegister() {
        printHeader("CUSTOMER REGISTRATION");
        try {
            System.out.print("Enter Desired Customer ID (number): ");
            int id = readIntChoice();
            System.out.print("Enter Full Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Email Address: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter Mobile Number: ");
            String mobile = scanner.nextLine().trim();
            System.out.print("Enter Delivery Address: ");
            String address = scanner.nextLine().trim();

            Customer customer = new Customer(id, name, email, mobile, address);
            customerService.registerCustomer(customer);
            System.out.println("\n[SUCCESS] Registration successful! You can now log in using Customer ID: " + id);
        } catch (Exception e) {
            System.out.println("\n[!] Registration failed: " + e.getMessage());
        }
    }

    private static void handleCustomerLogin() {
        printHeader("CUSTOMER LOGIN");
        System.out.print("Enter your Customer ID: ");
        int id = readIntChoice();

        try {
            currentCustomer = customerService.login(id);
            System.out.println("\n[SUCCESS] Welcome back, " + currentCustomer.getName() + "!");
        } catch (CustomerNotFoundException e) {
            System.out.println("\n[!] " + e.getMessage());
        }
    }

    private static boolean ensureLoggedIn() {
        if (currentCustomer == null) {
            System.out.println("\n[!] Please Login first (Option 2) or Register (Option 1) to perform this action.");
            return false;
        }
        return true;
    }

    /**
     * Requirement: Customers can search products using Name, Category, Brand, Price Range.
     * Uses Java 8 Stream API implementations in ProductService.
     */
    private static void handleCustomerSearchProduct() {
        printHeader("SEARCH PRODUCTS (JAVA 8 STREAM API)");
        System.out.println("1. Search by Name");
        System.out.println("2. Filter by Category");
        System.out.println("3. Filter by Brand");
        System.out.println("4. Filter by Price Range");
        System.out.println("5. Sort by Price (Low to High)");
        System.out.println("6. Sort by Price (High to Low)");
        System.out.print("Enter search option: ");

        int option = readIntChoice();
        switch (option) {
            case 1 -> {
                System.out.print("Enter product name: ");
                String name = scanner.nextLine().trim();
                displayProductList(productService.searchByName(name));
            }
            case 2 -> {
                System.out.print("Enter category: ");
                String cat = scanner.nextLine().trim();
                displayProductList(productService.filterByCategory(cat));
            }
            case 3 -> {
                System.out.print("Enter brand: ");
                String brand = scanner.nextLine().trim();
                displayProductList(productService.filterByBrand(brand));
            }
            case 4 -> {
                try {
                    System.out.print("Enter minimum price (Rs.): ");
                    double min = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Enter maximum price (Rs.): ");
                    double max = Double.parseDouble(scanner.nextLine().trim());
                    displayProductList(productService.filterByPriceRange(min, max));
                } catch (NumberFormatException e) {
                    System.out.println("[!] Invalid price input.");
                }
            }
            case 5 -> displayProductList(productService.sortByPrice(true));
            case 6 -> displayProductList(productService.sortByPrice(false));
            default -> System.out.println("[!] Invalid search option.");
        }
    }

    private static void handleAddToCart() {
        if (!ensureLoggedIn()) return;

        printHeader("ADD PRODUCT TO CART");
        try {
            System.out.print("Enter Product ID to add: ");
            int productId = readIntChoice();
            System.out.print("Enter Quantity: ");
            int quantity = readIntChoice();

            cartService.addToCart(currentCustomer.getCustomerId(), productId, quantity);
            System.out.println("\n[SUCCESS] Item successfully added to your cart!");
        } catch (ProductNotFoundException | InsufficientStockException | InvalidQuantityException e) {
            System.out.println("\n[!] " + e.getMessage());
        }
    }

    private static void handleViewCart() {
        if (!ensureLoggedIn()) return;

        printHeader("YOUR SHOPPING CART");
        List<CartItem> cartItems = cartService.getCartItems(currentCustomer.getCustomerId());

        if (cartItems.isEmpty()) {
            System.out.println("Your cart is currently empty.");
            return;
        }

        cartItems.forEach(System.out::println);
        double total = cartService.calculateCartTotal(currentCustomer.getCustomerId());
        System.out.println("-----------------------------------------------------------------");
        System.out.printf("Total Cart Value: Rs.%.2f\n", total);
        System.out.println("-----------------------------------------------------------------");

        // Sub-options to manage quantities or clear cart
        System.out.println("\nCart Options:");
        System.out.println("1. Increase Quantity");
        System.out.println("2. Decrease Quantity");
        System.out.println("3. Clear Entire Cart");
        System.out.println("4. Return to Customer Menu");
        System.out.print("Choose an option: ");

        int cartChoice = readIntChoice();
        try {
            switch (cartChoice) {
                case 1 -> {
                    System.out.print("Enter Product ID: ");
                    int pId = readIntChoice();
                    System.out.print("Quantity to add: ");
                    int addQty = readIntChoice();
                    cartService.increaseQuantity(currentCustomer.getCustomerId(), pId, addQty);
                    System.out.println("[SUCCESS] Quantity increased.");
                }
                case 2 -> {
                    System.out.print("Enter Product ID: ");
                    int pId = readIntChoice();
                    System.out.print("Quantity to subtract: ");
                    int subQty = readIntChoice();
                    cartService.decreaseQuantity(currentCustomer.getCustomerId(), pId, subQty);
                    System.out.println("[SUCCESS] Quantity decreased.");
                }
                case 3 -> {
                    cartService.clearCart(currentCustomer.getCustomerId());
                    System.out.println("[SUCCESS] Cart cleared.");
                }
                case 4 -> {}
                default -> System.out.println("[!] Invalid choice.");
            }
        } catch (Exception e) {
            System.out.println("\n[!] " + e.getMessage());
        }
    }

    private static void handleRemoveFromCart() {
        if (!ensureLoggedIn()) return;

        printHeader("REMOVE ITEM FROM CART");
        System.out.print("Enter Product ID to remove: ");
        int productId = readIntChoice();
        cartService.removeFromCart(currentCustomer.getCustomerId(), productId);
        System.out.println("\n[SUCCESS] Item removed from your cart.");
    }

    private static void handlePlaceOrder() {
        if (!ensureLoggedIn()) return;

        printHeader("CHECKOUT & PLACE ORDER");
        try {
            Order order = orderService.placeOrder(currentCustomer.getCustomerId());
            System.out.println("\n========================================================");
            System.out.println(" [SUCCESS] ORDER PLACED SUCCESSFULLY!");
            System.out.println("========================================================");
            System.out.println(order);
            System.out.println("\nDelivery to: " + currentCustomer.getName() + ", " + currentCustomer.getAddress());
            System.out.println("Cart has been cleared.");
        } catch (InvalidOrderException | InsufficientStockException e) {
            System.out.println("\n[!] Order placement failed: " + e.getMessage());
        }
    }

    private static void handleViewMyOrders() {
        if (!ensureLoggedIn()) return;

        printHeader("ORDER HISTORY FOR " + currentCustomer.getName().toUpperCase());
        List<Order> orders = orderService.getOrdersByCustomerId(currentCustomer.getCustomerId());

        if (orders.isEmpty()) {
            System.out.println("You have not placed any orders yet.");
        } else {
            for (Order o : orders) {
                System.out.println(o);
                System.out.println("   Items in this order:");
                o.getItems().forEach(item -> System.out.println("     - " + item));
                System.out.println("   -------------------------------------------------");
            }
        }
    }

    private static void handleCancelOrder() {
        if (!ensureLoggedIn()) return;

        printHeader("CANCEL ORDER");
        System.out.print("Enter Order ID to cancel: ");
        int orderId = readIntChoice();

        try {
            // Check order ownership
            Order order = orderService.getOrderById(orderId);
            if (order.getCustomerId() != currentCustomer.getCustomerId()) {
                System.out.println("\n[!] Unauthorized: This order does not belong to you.");
                return;
            }

            orderService.cancelOrder(orderId);
            System.out.println("\n[SUCCESS] Order #" + orderId + " has been cancelled. Inventory stock has been restored!");
        } catch (InvalidOrderException e) {
            System.out.println("\n[!] " + e.getMessage());
        }
    }

    // ========================================================================
    //                         HELPER METHODS
    // ========================================================================

    private static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(65));
        System.out.println("  " + title);
        System.out.println("=".repeat(65));
    }

    private static void displayProductList(List<Product> products) {
        if (products == null || products.isEmpty()) {
            System.out.println("\nNo products matched your criteria.");
        } else {
            System.out.println("\nMatching Products (" + products.size() + "):");
            products.forEach(System.out::println);
        }
    }

    /**
     * Reads integer input safely, consuming trailing newlines and preventing crashes on text.
     */
    private static int readIntChoice() {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("[!] Please enter a valid number: ");
            }
        }
    }
}
