package classes;

package classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReceiptSettingsLoader {
    public List<String> headerLines = new ArrayList<>();
    public List<String> footerLines = new ArrayList<>();

    public void loadSettings(String filename) {
        File settingsFile = new File(filename);
        if (!settingsFile.exists()) {
            System.out.println("Varovani: Soubor " + filename + " nenalezen.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(settingsFile))) {
            String currentSection = "";
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    if (line.startsWith("[")) {
                        currentSection = line;
                    } else if (currentSection.equals("[HEADER]")) {
                        headerLines.add(line);
                    } else if (currentSection.equals("[FOOTER]")) {
                        footerLines.add(line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Chyba pri cteni souboru: " + e.getMessage());
        }
    }
}