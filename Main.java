package org.example;
import java.util.*;
import java.time.LocalDate;
import java.util.*;
import java.time.LocalDate;

interface Shippable {
    String getName();
    double getWeight();
}

abstract class Product {
    String name;
    double price;
    int quantity;

    Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    abstract boolean isExpired();

    boolean isAvailable(int requestedQty) {
        return quantity >= requestedQty && !isExpired();
    }

    void reduceQuantity(int qty) {
        quantity -= qty;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}

class ExpirableProduct extends Product {
    LocalDate expiryDate;

    ExpirableProduct(String name, double price, int quantity, LocalDate expiryDate) {
        super(name, price, quantity);
        this.expiryDate = expiryDate;
    }

    @Override
    boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }
}

class NonExpirableProduct extends Product {
    NonExpirableProduct(String name, double price, int quantity) {
        super(name, price, quantity);
    }

    @Override
    boolean isExpired() {
        return false;
    }
}

class ShippableProduct extends ExpirableProduct implements Shippable {
    double weight;

    ShippableProduct(String name, double price, int quantity, LocalDate expiryDate, double weight) {
        super(name, price, quantity, expiryDate);
        this.weight = weight;
    }

    @Override
    public double getWeight() {
        return weight;
    }
}

class ShippableNonExpirableProduct extends NonExpirableProduct implements Shippable {
    double weight;

    ShippableNonExpirableProduct(String name, double price, int quantity, double weight) {
        super(name, price, quantity);
        this.weight = weight;
    }

    @Override
    public double getWeight() {
        return weight;
    }
}

class Customer {
    String name;
    double balance;

    Customer(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    boolean hasEnough(double amount) {
        return balance >= amount;
    }

    void deduct(double amount) {
        balance -= amount;
    }

    public double getBalance() {
        return balance;
    }
}

class CartItem {
    Product product;
    int quantity;

    CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    double getTotal() {
        return product.getPrice() * quantity;
    }

    boolean isShippable() {
        return product instanceof Shippable;
    }
}

class Cart {
    List<CartItem> items = new ArrayList<>();

    void add(Product product, int quantity) {
        if (!product.isAvailable(quantity)) {
            throw new IllegalArgumentException("Product not available or expired.");
        }
        items.add(new CartItem(product, quantity));
    }

    List<CartItem> getItems() {
        return items;
    }

    boolean isEmpty() {
        return items.isEmpty();
    }

    double getSubtotal() {
        return items.stream().mapToDouble(CartItem::getTotal).sum();
    }

    double getShippingFee() {
        return items.stream().filter(i -> i.isShippable()).mapToDouble(i -> 15).distinct().count() > 0 ? 30 : 0;
    }

    List<Shippable> getShippableItems() {
        List<Shippable> list = new ArrayList<>();
        for (CartItem item : items) {
            if (item.product instanceof Shippable) {
                for (int i = 0; i < item.quantity; i++) {
                    list.add((Shippable) item.product);
                }
            }
        }
        return list;
    }
}


class ShippingService {
    static void ship(List<Shippable> items) {
        if (items.isEmpty()) return;

        System.out.println("** Shipment notice **");
        Map<String, Integer> countMap = new LinkedHashMap<>();
        double totalWeight = 0;

        for (Shippable item : items) {
            countMap.put(item.getName(), countMap.getOrDefault(item.getName(), 0) + 1);
            totalWeight += item.getWeight();
        }

        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            System.out.println(entry.getValue() + "x " + entry.getKey());
        }
        System.out.printf("Total package weight %.1fkg%n", totalWeight);
    }
}

class CheckoutService {
    static void checkout(Customer customer, Cart cart) {
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        double subtotal = cart.getSubtotal();
        double shipping = cart.getShippingFee();
        double total = subtotal + shipping;

        if (!customer.hasEnough(total)) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        for (CartItem item : cart.getItems()) {
            if (!item.product.isAvailable(item.quantity)) {
                throw new IllegalArgumentException("Product expired or out of stock during checkout.");
            }
        }

        for (CartItem item : cart.getItems()) {
            item.product.reduceQuantity(item.quantity);
        }

        List<Shippable> shippables = cart.getShippableItems();
        ShippingService.ship(shippables);

        customer.deduct(total);

        System.out.println("** Checkout receipt **");
        for (CartItem item : cart.getItems()) {
            System.out.println(item.quantity + "x " + item.product.getName() + " " + item.getTotal());
        }
        System.out.println("----------------------");
        System.out.println("Subtotal " + subtotal);
        System.out.println("Shipping " + shipping);
        System.out.println("Amount " + total);
        System.out.println("Remaining Balance " + customer.getBalance());
    }
}


public class Main {
    public static void main(String[] args) {

        Customer customer = new Customer("Ahmed", 1000);

        Product cheese = new ShippableProduct("Cheese 200g", 100, 5, LocalDate.of(2025, 12, 1), 0.2);
        Product biscuits = new ShippableProduct("Biscuits 700g", 150, 2, LocalDate.of(2025, 10, 1), 0.7);
        Product tv = new ShippableNonExpirableProduct("TV", 300, 4, 5.0);
        Product scratchCard = new NonExpirableProduct("Mobile scratch card", 50, 10);

        Cart cart = new Cart();
        cart.add(cheese, 2);
        cart.add(biscuits, 1);
        cart.add(scratchCard, 1);

        CheckoutService.checkout(customer, cart);

    }
}
