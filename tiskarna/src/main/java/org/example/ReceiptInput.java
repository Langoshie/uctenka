package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReceiptInput {

    public static List<ReceiptItem> readItemsFromConsole() {
        Scanner scanner = new Scanner(System.in);
        List<ReceiptItem> items = new ArrayList<>();

        System.out.println("Zadávejte položky nákupu. Pro ukončení napište 'konec'.");

        while (true) {
            System.out.print("Název položky: ");
            String name = scanner.nextLine().trim();

            // Kontrola ukončení
            if (name.equalsIgnoreCase("konec")) {
                break;
            }

            if (name.isEmpty()) {
                System.out.println("Název nesmí být prázdný, zkuste to znovu.");
                continue;
            }

            double price = 0;
            int quantity = 0;

            // Načítání čísel s ochranou proti špatnému zadání (text místo čísla)
            try {
                System.out.print("Cena za kus: ");
                price = Double.parseDouble(scanner.nextLine().trim());

                System.out.print("Množství: ");
                quantity = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Chyba: Cena i množství musí být číslo. Zadejte položku znovu.");
                continue;
            }

            items.add(new ReceiptItem(name, price, quantity));
            System.out.println("--> Přidáno: " + name + " (" + quantity + " ks, " + price + " Kč/ks)");
        }

        return items;
    }
}
