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
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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

    // Customer ID 0 is designated for guest / non-logged-in cart sessions
    private static final int GUEST_CUSTOMER_ID = 0;

    private static int getActiveCustomerId() {
        return currentCustomer != null ? currentCustomer.getCustomerId() : GUEST_CUSTOMER_ID;
    }

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
        displayProductList(products);
    }

    private static void handleAdminSearchProduct() {
        printHeader("SEARCH PRODUCT (ADMIN)");

        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products available in the store.");
            return;
        }

        // Show Available Categories
        Set<String> categories = products.stream()
                .map(Product::getCategory)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));
        System.out.println("CATEGORIES: " + String.join(" | ", categories));

        // Display Products List Table
        displayProductList(products);

        System.out.print("\nEnter Product Name, Category, or ID to search: ");
        String query = scanner.nextLine().trim();
        if (query.isEmpty()) {
            System.out.println("[!] Search query cannot be empty.");
            return;
        }

        // 1. Try search by numeric ID first
        try {
            int id = Integer.parseInt(query);
            try {
                Product p = productService.getProductById(id);
                System.out.println("\nFound Product:\n" + p);
                return;
            } catch (ProductNotFoundException ignored) {}
        } catch (NumberFormatException ignored) {}

        // 2. Search by Product Name
        List<Product> nameMatches = productService.searchByName(query);
        if (!nameMatches.isEmpty()) {
            displayProductList(nameMatches);
            return;
        }

        // 3. Search by Category
        List<Product> catMatches = productService.filterByCategory(query);
        if (!catMatches.isEmpty()) {
            displayProductList(catMatches);
            return;
        }

        System.out.println("\n[!] No products found matching: '" + query + "'.");
    }

    private static void handleUpdateProduct() {
        printHeader("UPDATE PRODUCT");

        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products available in the store to update.");
            return;
        }

        Set<String> categories = products.stream()
                .map(Product::getCategory)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));
        System.out.println("CATEGORIES: " + String.join(" | ", categories));
        displayProductList(products);

        System.out.print("\nEnter Product Name or ID to update: ");
        String input = scanner.nextLine().trim();

        try {
            Product p = resolveProduct(input);
            System.out.println("\nSelected: " + p);

            System.out.println("1. Update Price");
            System.out.println("2. Update Quantity (Stock)");
            System.out.print("Enter your choice: ");
            int option = readIntChoice();

            if (option == 1) {
                System.out.print("Enter new Price (Rs.): ");
                double newPrice = Double.parseDouble(scanner.nextLine().trim());
                productService.updateProductPrice(p.getProductId(), newPrice);
                System.out.println("[SUCCESS] Product price updated successfully!");
            } else if (option == 2) {
                System.out.print("Enter new Quantity: ");
                int newQty = readIntChoice();
                productService.updateProductQuantity(p.getProductId(), newQty);
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

        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products available in the store to delete.");
            return;
        }

        Set<String> categories = products.stream()
                .map(Product::getCategory)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));
        System.out.println("CATEGORIES: " + String.join(" | ", categories));
        displayProductList(products);

        System.out.print("\nEnter Product Name or ID to delete: ");
        String input = scanner.nextLine().trim();

        try {
            Product p = resolveProduct(input);
            productService.deleteProduct(p.getProductId());
            System.out.println("\n[SUCCESS] Product '" + p.getProductName() + "' deleted successfully!");
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
            printHeader("CUSTOMER MENU" + (currentCustomer != null ? " [Logged in as: " + currentCustomer.getName() + "]" : " [Guest Mode]"));
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
            System.out.println("11. " + (currentCustomer != null ? "Logout & Back to Main Menu" : "Back to Main Menu"));
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
                    if (currentCustomer != null) {
                        System.out.println("\n[SUCCESS] Logged out from " + currentCustomer.getName() + ".");
                    }
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
            // Merge guest cart items into customer cart if any were added prior to login
            mergeGuestCartToCustomerCart();
        } catch (CustomerNotFoundException e) {
            System.out.println("\n[!] " + e.getMessage());
        }
    }

    /**
     * Merges items added while browsing as a guest into the logged-in customer's shopping cart.
     */
    private static void mergeGuestCartToCustomerCart() {
        if (currentCustomer == null) return;
        List<CartItem> guestItems = cartService.getCartItems(GUEST_CUSTOMER_ID);
        if (!guestItems.isEmpty()) {
            int mergedCount = 0;
            for (CartItem item : guestItems) {
                try {
                    cartService.addToCart(currentCustomer.getCustomerId(), item.getProductId(), item.getQuantity());
                    mergedCount++;
                } catch (Exception e) {
                    System.out.println("[!] Notice: Could not transfer " + item.getProductName() + " to account cart: " + e.getMessage());
                }
            }
            cartService.clearCart(GUEST_CUSTOMER_ID);
            if (mergedCount > 0) {
                System.out.println("[INFO] " + mergedCount + " item(s) from your guest session were merged into your account cart!");
            }
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
        printHeader("SEARCH PRODUCTS");
        System.out.print("Enter Product Name or keyword to search: ");
        String name = scanner.nextLine().trim();
        List<Product> list = productService.searchByName(name);
        displayProductList(list);

        System.out.println("\nAdditional Filters & Sorting (Optional):");
        System.out.println("1. Filter by Category");
        System.out.println("2. Filter by Brand");
        System.out.println("3. Filter by Price Range");
        System.out.println("4. Sort by Price (Low to High)");
        System.out.println("5. Sort by Price (High to Low)");
        System.out.println("6. Done / Return to Customer Menu");
        System.out.print("Enter option (or 6 to return): ");

        int option = readIntChoice();
        switch (option) {
            case 1 -> {
                System.out.print("Enter category: ");
                String cat = scanner.nextLine().trim();
                displayProductList(productService.filterByCategory(cat));
            }
            case 2 -> {
                System.out.print("Enter brand: ");
                String brand = scanner.nextLine().trim();
                displayProductList(productService.filterByBrand(brand));
            }
            case 3 -> {
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
            case 4 -> displayProductList(productService.sortByPrice(true));
            case 5 -> displayProductList(productService.sortByPrice(false));
            case 6 -> {}
            default -> {}
        }
    }

    private static void handleAddToCart() {
        printHeader("ADD PRODUCT TO CART" + (currentCustomer == null ? " [Guest Mode]" : ""));

        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products available in the store right now.");
            return;
        }

        // 1. Display available categories
        Set<String> categories = products.stream()
                .map(Product::getCategory)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));
        System.out.println("CATEGORIES: " + String.join(" | ", categories));

        // 2. Display the products list table
        displayProductList(products);

        // 3. Prompt user for product name or ID
        try {
            System.out.print("\nEnter Product Name (or ID) to add: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return;

            // If user entered a category name, filter down and ask again
            for (String cat : categories) {
                if (cat.equalsIgnoreCase(input)) {
                    List<Product> catProducts = productService.filterByCategory(cat);
                    System.out.println("\n--- Products in " + cat + " ---");
                    displayProductList(catProducts);
                    System.out.print("\nEnter Product Name (or ID) from " + cat + " to add: ");
                    input = scanner.nextLine().trim();
                    break;
                }
            }

            Product product = resolveProduct(input);
            System.out.println("Selected: " + product.getProductName() + " | Price: Rs. " + product.getPrice() + " | In Stock: " + product.getQuantity());

            System.out.print("Enter Quantity: ");
            int quantity = readIntChoice();

            cartService.addToCart(getActiveCustomerId(), product.getProductId(), quantity);
            System.out.println("\n[SUCCESS] '" + product.getProductName() + "' (x" + quantity + ") added to your cart!");
            if (currentCustomer == null) {
                System.out.println("[NOTE] Added to guest cart. You can view your cart or log in whenever you want to place the order.");
            }
        } catch (ProductNotFoundException | InsufficientStockException | InvalidQuantityException e) {
            System.out.println("\n[!] " + e.getMessage());
        }
    }

    private static void handleViewCart() {
        int customerId = getActiveCustomerId();
        printHeader("YOUR SHOPPING CART" + (currentCustomer == null ? " [Guest Cart]" : ""));
        List<CartItem> cartItems = cartService.getCartItems(customerId);

        if (cartItems.isEmpty()) {
            System.out.println("Your cart is currently empty.");
            return;
        }

        cartItems.forEach(System.out::println);
        double total = cartService.calculateCartTotal(customerId);
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
                    System.out.print("Enter Product Name (or ID): ");
                    String input = scanner.nextLine().trim();
                    Product p = resolveProduct(input);
                    System.out.print("Quantity to add: ");
                    int addQty = readIntChoice();
                    cartService.increaseQuantity(customerId, p.getProductId(), addQty);
                    System.out.println("[SUCCESS] Quantity increased for " + p.getProductName() + ".");
                }
                case 2 -> {
                    System.out.print("Enter Product Name (or ID): ");
                    String input = scanner.nextLine().trim();
                    Product p = resolveProduct(input);
                    System.out.print("Quantity to subtract: ");
                    int subQty = readIntChoice();
                    cartService.decreaseQuantity(customerId, p.getProductId(), subQty);
                    System.out.println("[SUCCESS] Quantity decreased for " + p.getProductName() + ".");
                }
                case 3 -> {
                    cartService.clearCart(customerId);
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
        int customerId = getActiveCustomerId();
        printHeader("REMOVE ITEM FROM CART" + (currentCustomer == null ? " [Guest Cart]" : ""));
        List<CartItem> cartItems = cartService.getCartItems(customerId);
        if (cartItems.isEmpty()) {
            System.out.println("Your cart is currently empty.");
            return;
        }

        System.out.println("Items currently in your cart:");
        cartItems.forEach(System.out::println);

        try {
            System.out.print("\nEnter Product Name (or ID) to remove: ");
            String input = scanner.nextLine().trim();
            Product product = resolveProduct(input);
            cartService.removeFromCart(customerId, product.getProductId());
            System.out.println("\n[SUCCESS] '" + product.getProductName() + "' removed from your cart.");
        } catch (ProductNotFoundException e) {
            System.out.println("\n[!] " + e.getMessage());
        }
    }

    private static void handlePlaceOrder() {
        if (currentCustomer == null) {
            List<CartItem> guestItems = cartService.getCartItems(GUEST_CUSTOMER_ID);
            if (guestItems.isEmpty()) {
                System.out.println("\n[!] Your cart is empty! Add products to cart first.");
                return;
            }
            System.out.println("\n[!] You must be logged in to place an order and checkout.");
            System.out.println("1. Login with existing Customer ID");
            System.out.println("2. Register a new Customer Account");
            System.out.println("3. Return to Menu");
            System.out.print("Choose an option: ");
            int option = readIntChoice();
            if (option == 1) {
                handleCustomerLogin();
                if (currentCustomer == null) return;
            } else if (option == 2) {
                handleCustomerRegister();
                System.out.println("\nPlease log in with your Customer ID to complete your order.");
                handleCustomerLogin();
                if (currentCustomer == null) return;
            } else {
                return;
            }
        }

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

    /**
     * Resolves a Product by either its Product Name or its numeric ID.
     * Supports case-insensitive matching and partial search.
     */
    private static Product resolveProduct(String input) throws ProductNotFoundException {
        if (input == null || input.trim().isEmpty()) {
            throw new ProductNotFoundException("Product identifier cannot be empty.");
        }
        input = input.trim();

        // 1. If user typed numeric ID, look up by ID first
        try {
            int id = Integer.parseInt(input);
            try {
                return productService.getProductById(id);
            } catch (ProductNotFoundException ignored) {}
        } catch (NumberFormatException ignored) {}

        // 2. Search by Product Name (case-insensitive substring match)
        List<Product> matches = productService.searchByName(input);
        if (matches.isEmpty()) {
            throw new ProductNotFoundException("No product found matching '" + input + "'.");
        }

        // Exact match priority (case-insensitive)
        for (Product p : matches) {
            if (p.getProductName().equalsIgnoreCase(input)) {
                return p;
            }
        }

        // Single partial match
        if (matches.size() == 1) {
            return matches.get(0);
        }

        // Multiple partial matches: display and prompt user to select
        System.out.println("\nMultiple products matched '" + input + "':");
        for (int i = 0; i < matches.size(); i++) {
            Product p = matches.get(i);
            System.out.printf("  %d. %s (Price: Rs.%.2f | Stock: %d | ID: %d)\n",
                    i + 1, p.getProductName(), p.getPrice(), p.getQuantity(), p.getProductId());
        }
        System.out.print("Select product number (1-" + matches.size() + "): ");
        int selection = readIntChoice();
        if (selection >= 1 && selection <= matches.size()) {
            return matches.get(selection - 1);
        }
        throw new ProductNotFoundException("Invalid product selection.");
    }

    private static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(65));
        System.out.println("  " + title);
        System.out.println("=".repeat(65));
    }

    private static void displayProductList(List<Product> products) {
        if (products == null || products.isEmpty()) {
            System.out.println("\n[!] No products matched your criteria.");
            return;
        }

        System.out.println("\n" + "-".repeat(88));
        System.out.printf("| %-4s | %-20s | %-13s | %-10s | %-14s | %-8s |\n",
                "ID", "Product Name", "Category", "Brand", "Price", "Stock");
        System.out.println("-".repeat(88));
        for (Product p : products) {
            System.out.printf("| %-4d | %-20s | %-13s | %-10s | Rs.%-11.2f | %-8d |\n",
                    p.getProductId(), p.getProductName(), p.getCategory(), p.getBrand(), p.getPrice(), p.getQuantity());
        }
        System.out.println("-".repeat(88));
        System.out.println("Total products shown: " + products.size());
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
