package org.example;

public class ReceiptItem {
    private String name;
    private double price;
    private int quantity;

    public ReceiptItem(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    // Metoda pro celkovou cenu položky (hodí se pro tisk)
    public double getTotalPrice() {
        return price * quantity;
    }
}
