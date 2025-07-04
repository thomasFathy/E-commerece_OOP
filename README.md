# Shopping Cart System

## 📋 Overview

This project implements a basic **shopping cart and checkout system** with support for:

- Expirable and non-expirable products
- Shippable and non-shippable products
- Product quantity management
- Customer balance verification
- Shipping fee calculations
- Console-based checkout receipt and shipment summary

The solution is implemented in **Java** and demonstrates **object-oriented programming**, **interface usage**, and **robust error handling**.

---

## 📦 Features

- ✅ Define products with `name`, `price`, `quantity`, and optionally `expiry date` and `weight`
- ✅ Distinguish between:
  - Expirable vs Non-expirable products
  - Shippable vs Non-shippable products
- ✅ Cart system:
  - Add product with quantity (validated against stock and expiry)
- ✅ Checkout system:
  - Calculates subtotal and shipping
  - Checks for:
    - Empty cart
    - Insufficient customer balance
    - Expired or out-of-stock products
- ✅ Shipping service interface (`Shippable`)
  - Collects items and displays a shipment summary

---

## 🛠️ How to Run

### Requirements
- Java 8 or higher
- Any IDE (e.g., IntelliJ IDEA, Eclipse) or command line

### Steps

1. Clone or download the project
2. Open in your IDE or compile via terminal
3. Run the `Main.java` file to simulate the scenario

```bash
javac Main.java
java Main
