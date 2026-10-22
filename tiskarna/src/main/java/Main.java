import classes.ReceiptInput;
import classes.ReceiptItem;
import classes.print;
import classes.ReceiptSettingsLoader;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ReceiptSettingsLoader loader = new ReceiptSettingsLoader();
        loader.loadSettings("receipt_settings.txt");

        String storeName = loader.headerLines.isEmpty() ? "Neznamy Obchod" : loader.headerLines.get(0);

        List<ReceiptItem> inputItems = ReceiptInput.readItemsFromConsole();

        if (inputItems.isEmpty()) {
            System.out.println("Nebyly zadany zadne polozky. Ukoncuji.");
            return;
        }

        List<print.Item> printItems = new ArrayList<>();
        for (ReceiptItem item : inputItems) {
            printItems.add(new print.Item(item.getName(), item.getQuantity(), item.getPrice()));
        }

        System.out.println("\n");
        print.printReceipt(storeName, printItems);

        if (!loader.footerLines.isEmpty()) {
            for (String footerLine : loader.footerLines) {
                System.out.println("      " + footerLine + "      ");
            }
        }
    }
}