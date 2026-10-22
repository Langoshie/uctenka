package tests;

import classes.ReceiptSettingsLoader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReceiptSettingsLoaderTest {

    private ReceiptSettingsLoader loader;

    @BeforeEach
    void setUp() {
        loader = new ReceiptSettingsLoader();
    }

    @Test
    @DisplayName("Nová instance má prázdné seznamy pro hlavičku i patičku")
    void testInitialStateIsEmpty() {
        assertNotNull(loader.headerLines);
        assertNotNull(loader.footerLines);
        assertTrue(loader.headerLines.isEmpty());
        assertTrue(loader.footerLines.isEmpty());
    }

    @Test
    @DisplayName("Úspěšné načtení hlavičky a patičky z platného souboru")
    void testLoadSettingsSuccess(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("settings.txt");
        String content = """
                [HEADER]
                Supermarket VŠE
                Náměstí W. Churchilla 4
                IČO: 12345678
                [FOOTER]
                Děkujeme za nákup!
                Na shledanou.
                """;
        Files.writeString(filePath, content);

        loader.loadSettings(filePath.toString());

        // Kontrola hlavičky
        assertEquals(3, loader.headerLines.size());
        assertEquals(List.of(
                "Supermarket VŠE",
                "Náměstí W. Churchilla 4",
                "IČO: 12345678"
        ), loader.headerLines);

        // Kontrola patičky
        assertEquals(2, loader.footerLines.size());
        assertEquals(List.of(
                "Děkujeme za nákup!",
                "Na shledanou."
        ), loader.footerLines);
    }

    @Test
    @DisplayName("Neexistující soubor nevyhodí výjimku a seznamy zůstanou prázdné")
    void testNonExistentFileHandledGracefully() {
        String nonExistentPath = "soubor_ktery_urcite_neexistuje_123456.txt";

        // Metoda nesmí spadnout na výjimku
        assertDoesNotThrow(() -> loader.loadSettings(nonExistentPath));

        // Seznamy musí zůstat netknuté
        assertTrue(loader.headerLines.isEmpty());
        assertTrue(loader.footerLines.isEmpty());
    }

    @Test
    @DisplayName("Ignorování prázdných řádků a ořezávání přebytečných mezer okolo textu")
    void testIgnoreEmptyLinesAndTrimWhitespace(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("spaced_settings.txt");
        String content = """
                
                [HEADER]
                   Obchod u Nádraží   
                
                   Tel: 123 456 789   
                
                [FOOTER]
                   Přejeme hezký den!   
                """;
        Files.writeString(filePath, content);

        loader.loadSettings(filePath.toString());

        assertEquals(List.of("Obchod u Nádraží", "Tel: 123 456 789"), loader.headerLines);
        assertEquals(List.of("Přejeme hezký den!"), loader.footerLines);
    }

    @Test
    @DisplayName("Text před první sekcí a neznámé sekce se neuloží do hlavičky ani patičky")
    void testIgnoreUnknownSectionsAndPrefixedText(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("extra_sections.txt");
        String content = """
                # Toto je komentář před sekcí
                Nějaký náhodný text
                [HEADER]
                Platná hlavička
                [CUSTOM_INFO]
                Tento řádek patří do neznámé sekce
                [FOOTER]
                Platná patička
                """;
        Files.writeString(filePath, content);

        loader.loadSettings(filePath.toString());

        assertEquals(1, loader.headerLines.size());
        assertEquals("Platná hlavička", loader.headerLines.get(0));

        assertEquals(1, loader.footerLines.size());
        assertEquals("Platná patička", loader.footerLines.get(0));
    }
}