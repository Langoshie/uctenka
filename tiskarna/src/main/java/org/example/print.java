package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class print {

    public record Item(String name, int quantity, double unitPrice) {
        public double totalPrice() {
            return quantity * unitPrice;
        }
    }

    public static void printReceipt(String storeName, List<Item> items) {
        int receiptWidth = 40;
        String separator = "-".repeat(receiptWidth);
        String doubleSeparator = "=".repeat(receiptWidth);

        System.out.println(separator);

        int paddingBefore = (receiptWidth - storeName.length()) / 2;
        String header = " ".repeat(Math.max(0, paddingBefore)) + storeName;
        System.out.println(header);
        System.out.println(separator);

        double total = 0;

        for (Item item : items) {
            String itemDetail = String.format(Locale.US, "%dx %.2f %s", item.quantity(), item.unitPrice(), item.name());
            String itemTotalStr = String.format(Locale.US, "%.2f", item.totalPrice());

            int remainingSpaces = receiptWidth - itemDetail.length() - itemTotalStr.length();
            if (remainingSpaces < 1) remainingSpaces = 1;

            System.out.println(itemDetail + " ".repeat(remainingSpaces) + itemTotalStr);
            total += item.totalPrice();
        }

        System.out.println(doubleSeparator);

        String totalLine = String.format(Locale.US, "TOTAL: %.2f CZK", total);
        int paddingTotal = receiptWidth - totalLine.length();
        System.out.println(" ".repeat(Math.max(0, paddingTotal)) + totalLine);

        System.out.println(separator);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        System.out.println("Date and time: " + dtf.format(LocalDateTime.now()));
        System.out.println("      Thank you for your purchase!      ");
        System.out.println(separator);
    }

    public static void main(String[] args) {
        List<Item> cart = List.of(
                new Item("Bread roll", 5, 20),
                new Item("Milk 1L", 1, 23),
                new Item("Chocolate bar", 2, 60),
                new Item("Plastic bag", 1, 2)
        );
    }
}