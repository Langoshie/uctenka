//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReceiptSettingsLoader {
    public ReceiptSettingsLoader() {
    }

    public static void main(String[] args) {
        File settingsFile = new File("receipt_settings.txt");
        List<String> headerLines = new ArrayList();
        List<String> footerLines = new ArrayList();

        try (BufferedReader br = new BufferedReader(new FileReader(settingsFile))) {
            String currentSection = "";

            String line;
            while((line = br.readLine()) != null) {
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

            System.out.println("Opakujici se data byla uspesne nactena ze souboru.");
        } catch (IOException e) {
            System.err.println("Chyba pri cteni souboru: " + e.getMessage());
            return;
        }

        System.out.println("\n--- NACTENA HLAVICKA ---");

        for(String h : headerLines) {
            System.out.println(h);
        }

        System.out.println("\n--- NACTENA PATKA ---");

        for(String f : footerLines) {
            System.out.println(f);
        }

    }
}
