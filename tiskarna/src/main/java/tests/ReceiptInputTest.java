package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.example.ReceiptItem;
import org.example.ReceiptInput;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReceiptInputTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        // Potlačení výpisů do konzole během testů, aby nezahlcovaly výstup
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
    }

    @AfterEach
    void tearDown() {
        // Obnovení systémových proudů do původního stavu
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private void setSimulatedInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    // =========================================================================
    // Základní průchody a ukončení
    // =========================================================================

    @Test
    @DisplayName("Okamžité zadání 'konec' vrátí prázdný seznam")
    void testImmediateExit() {
        setSimulatedInput("konec\n");

        List<ReceiptItem> items = ReceiptInput.readItemsFromConsole();

        assertNotNull(items);
        assertTrue(items.isEmpty(), "Seznam položek musí být prázdný");
    }

    @Test
    @DisplayName("Příkaz 'konec' funguje bez ohledu na velikost písmen (KONEC)")
    void testCaseInsensitiveExit() {
        setSimulatedInput("KONEC\n");

        List<ReceiptItem> items = ReceiptInput.readItemsFromConsole();

        assertTrue(items.isEmpty());
    }

    @Test
    @DisplayName("Načtení jedné položky se správně nastavenými atributy")
    void testReadSingleValidItem() {
        // Vstup: Název -> Cena -> Množství -> Ukončení
        String simulatedInput = String.join("\n",
                "Rohlík",
                "2.50",
                "10",
                "konec"
        ) + "\n";

        setSimulatedInput(simulatedInput);

        List<ReceiptItem> items = ReceiptInput.readItemsFromConsole();

        assertEquals(1, items.size());
        ReceiptItem item = items.get(0);

        assertEquals("Rohlík", item.getName());
        assertEquals(2.50, item.getPrice(), 0.001);
        assertEquals(10, item.getQuantity());
        assertEquals(25.00, item.getTotalPrice(), 0.001);
    }

    @Test
    @DisplayName("Načtení více položek zachová jejich pořadí a hodnoty")
    void testReadMultipleItems() {
        String simulatedInput = String.join("\n",
                "Chléb",
                "45.0",
                "1",
                "Máslo",
                "59.90",
                "2",
                "konec"
        ) + "\n";

        setSimulatedInput(simulatedInput);

        List<ReceiptItem> items = ReceiptInput.readItemsFromConsole();

        assertEquals(2, items.size());

        // První položka
        assertEquals("Chléb", items.get(0).getName());
        assertEquals(45.0, items.get(0).getPrice(), 0.001);
        assertEquals(1, items.get(0).getQuantity());

        // Druhá položka
        assertEquals("Máslo", items.get(1).getName());
        assertEquals(59.90, items.get(1).getPrice(), 0.001);
        assertEquals(2, items.get(1).getQuantity());
    }

    // =========================================================================
    // Ošetření nevalidních vstupů
    // =========================================================================

    @Test
    @DisplayName("Prázdný název položky (Enter / mezery) se přeskočí a cyklus pokračuje")
    void testEmptyNameSkipped() {
        String simulatedInput = String.join("\n",
                "",            // Pouze Enter
                "   ",         // Pouze mezery
                "Mléko",       // Platný název
                "22.90",
                "1",
                "konec"
        ) + "\n";

        setSimulatedInput(simulatedInput);

        List<ReceiptItem> items = ReceiptInput.readItemsFromConsole();

        assertEquals(1, items.size());
        assertEquals("Mléko", items.get(0).getName());
    }

    @Test
    @DisplayName("Neplatný formát ceny vyvolá ošetření chyby a umožní zadání znovu")
    void testInvalidPriceHandledGracefully() {
        String simulatedInput = String.join("\n",
                "Káva",
                "dvacet",      // Chybná cena (vyvolá NumberFormatException)
                "Káva",        // Nový pokus
                "150.0",
                "1",
                "konec"
        ) + "\n";

        setSimulatedInput(simulatedInput);

        List<ReceiptItem> items = ReceiptInput.readItemsFromConsole();

        assertEquals(1, items.size());
        assertEquals("Káva", items.get(0).getName());
        assertEquals(150.0, items.get(0).getPrice(), 0.001);
    }

    @Test
    @DisplayName("Neplatný formát množství vyvolá ošetření chyby a cyklus pokračuje")
    void testInvalidQuantityHandledGracefully() {
        String simulatedInput = String.join("\n",
                "Čokoláda",
                "35.0",
                "dvě",         // Chybné množství
                "Čokoláda",    // Nový pokus
                "35.0",
                "2",
                "konec"
        ) + "\n";

        setSimulatedInput(simulatedInput);

        List<ReceiptItem> items = ReceiptInput.readItemsFromConsole();

        assertEquals(1, items.size());
        assertEquals(2, items.get(0).getQuantity());
    }
}