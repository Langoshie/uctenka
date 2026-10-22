package tests; // Doporučeno mít stejný package jako testovaná třída (v src/test/java)

import org.example.print;
import org.example.print.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class printTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private String getOutput() {
        return outContent.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Item.totalPrice správně spočítá celkovou cenu položky")
    void testItemTotalPrice() {
        Item item1 = new Item("Rohlík", 5, 2.50);
        assertEquals(12.50, item1.totalPrice(), 0.001);

        Item item2 = new Item("Nulové množství", 0, 100.0);
        assertEquals(0.0, item2.totalPrice(), 0.001);
    }

    @Test
    @DisplayName("printReceipt vytiskne hlavičku, položky, oddělovače a správný součet")
    void testPrintReceiptStandardCart() {
        List<Item> cart = List.of(
                new Item("Bread roll", 5, 20.0),      // 100.00
                new Item("Milk 1L", 1, 23.0),          //  23.00
                new Item("Chocolate bar", 2, 60.0),    // 120.00
                new Item("Plastic bag", 1, 2.0)        //   2.00
        );

        print.printReceipt("Moje Prodejna", cart);

        String output = getOutput();

        assertTrue(output.contains("Moje Prodejna"), "Účtenka musí obsahovat název obchodu");
        assertTrue(output.contains("Thank you for your purchase!"), "Účtenka musí obsahovat poděkování");
        assertTrue(output.contains("Date and time:"), "Účtenka musí obsahovat datum a čas");


        assertTrue(output.contains("----------------------------------------"));
        assertTrue(output.contains("========================================"));


        assertTrue(output.contains("5x 20.00 Bread roll"));
        assertTrue(output.contains("100.00"));
        assertTrue(output.contains("1x 23.00 Milk 1L"));
        assertTrue(output.contains("23.00"));

        assertTrue(output.contains("TOTAL: 245.00 CZK"), "Celková suma musí být 245.00 CZK");
    }

    @Test
    @DisplayName("printReceipt si poradí s prázdným nákupním košíkem")
    void testPrintReceiptEmptyCart() {
        print.printReceipt("Prázdný Obchod", List.of());

        String output = getOutput();

        assertTrue(output.contains("Prázdný Obchod"));
        assertTrue(output.contains("TOTAL: 0.00 CZK"), "Prázdný košík musí mít celkovou cenu 0.00 CZK");
    }

    @Test
    @DisplayName("printReceipt správně formátuje desetinná čísla na 2 desetinná místa")
    void testPrintReceiptDecimalFormatting() {
        List<Item> cart = List.of(
                new Item("Jablka", 3, 14.333) // 3 * 14.333 = 42.999 -> zaokrouhleno na 43.00
        );

        print.printReceipt("Ovoce", cart);

        String output = getOutput();

        assertTrue(output.contains("3x 14.33 Jablka"));
        assertTrue(output.contains("43.00"));
        assertTrue(output.contains("TOTAL: 43.00 CZK"));
    }
}