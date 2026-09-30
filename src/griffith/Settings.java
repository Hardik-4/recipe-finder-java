package griffith;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Persists the user's last ingredient preference between runs. */
public class Settings {
    private static final String FILE = "settings.txt";

    public static void save(String ingredient) {
        try (FileWriter writer = new FileWriter(FILE)) {
            writer.write(ingredient.trim().toLowerCase());
        } catch (IOException e) {
            System.out.println("Failed to save settings.");
        }
    }

    public static String load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line = reader.readLine();
            if (line == null || line.isBlank()) {
                return null;
            }
            return line.trim().toLowerCase();
        } catch (IOException e) {
            return null;
        }
    }

    public static void clear() {
        try {
            Files.deleteIfExists(Path.of(FILE));
        } catch (IOException e) {
            System.out.println("Failed to clear settings.");
        }
    }
}
