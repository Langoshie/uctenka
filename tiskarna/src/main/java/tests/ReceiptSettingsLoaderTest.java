package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import classes.ReceiptSettingsLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class ReceiptSettingsLoaderTest {

    private final File settingsFile = new File("receipt_settings.txt");
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        // Přesměrování standardního i chybového výstupu
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(errContent, true, StandardCharsets.UTF_8));

        // Zajištění čistého stavu před každým testem
        if (settingsFile.exists()) {
            settingsFile.delete();
        }
    }

    @AfterEach
    void tearDown() {
        // Obnovení výchozích proudů
        System.setOut(originalOut);
        System.setErr(originalErr);

        // Úklid souboru po testu
        if (settingsFile.exists()) {
            settingsFile.delete();
        }
    }

    @Test
    @DisplayName("Konstruktor lze vytvořit (pokrytí pro code coverage)")
    void testConstructor() {
        ReceiptSettingsLoader loader = new ReceiptSettingsLoader();
        assertNotNull(loader);
    }

    @Test
    @DisplayName("main úspěšně načte a vytiskne hlavičku i patičku")
    void testMainSuccess() throws IOException {
        String fileContent = """
                [HEADER]
                Supermarket VŠE
                Náměstí W. Churchilla 4
                IČO: 12345678
                [FOOTER]
                Děkujeme za nákup!
                """;
        Files.writeString(settingsFile.toPath(), fileContent);

        ReceiptSettingsLoader.main(new String[0]);

        String output = outContent.toString(StandardCharsets.UTF_8);

        assertTrue(output.contains("Opakujici se data byla uspesne nactena ze souboru."));
        assertTrue(output.contains("--- NACTENA HLAVICKA ---"));
        assertTrue(output.contains("Supermarket VŠE"));
        assertTrue(output.contains("Náměstí W. Churchilla 4"));
        assertTrue(output.contains("IČO: 12345678"));
        assertTrue(output.contains("--- NACTENA PATKA ---"));
        assertTrue(output.contains("Děkujeme za nákup!"));
    }

    @Test
    @DisplayName("main vypíše chybu do System.err, pokud soubor neexistuje")
    void testMainFileNotFound() {
        // Soubor receipt_settings.txt neexistuje

        ReceiptSettingsLoader.main(new String[0]);

        String errorOutput = errContent.toString(StandardCharsets.UTF_8);
        String standardOutput = outContent.toString(StandardCharsets.UTF_8);

        // Ověření, že chyba šla do chybového výstupu
        assertTrue(errorOutput.contains("Chyba pri cteni souboru:"));
        // Ověření, že se program předčasně ukončil přes return a netiskl hlavičky
        assertFalse(standardOutput.contains("--- NACTENA HLAVICKA ---"));
    }

    @Test
    @DisplayName("main správně ignoruje řádky mimo sekce a prázdné řádky")
    void testMainIgnoresUnknownSectionsAndEmptyLines() throws IOException {
        String fileContent = """
                Komentář před sekcí
                
                [HEADER]
                Platný název obchodu
                
                [UNKNOWN_SECTION]
                Ignorovaný text
                """;
        Files.writeString(settingsFile.toPath(), fileContent);

        ReceiptSettingsLoader.main(new String[0]);

        String output = outContent.toString(StandardCharsets.UTF_8);

        assertTrue(output.contains("Platný název obchodu"));
        assertFalse(output.contains("Komentář před sekcí"));
        assertFalse(output.contains("Ignorovaný text"));
    }
}