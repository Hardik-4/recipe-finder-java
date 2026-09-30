package griffith;

import java.util.List;
import java.util.Scanner;

import griffith.RecipeAPI.MealSummary;

/** Console UI helpers for the recipe finder. */
public class UI {
    private static final Scanner scanner = new Scanner(System.in);

    public static void banner() {
        System.out.println("=================================");
        System.out.println("     Recipe Finder (TheMealDB)   ");
        System.out.println("=================================");
    }

    public static void menu() {
        System.out.println();
        System.out.println("1) Search by ingredient");
        System.out.println("2) Random recipe");
        System.out.println("3) Show offline cache");
        System.out.println("4) Clear saved ingredient");
        System.out.println("5) Exit");
        System.out.print("Choose: ");
    }

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static String getIngredient() {
        return readLine("Enter an ingredient: ").toLowerCase();
    }

    public static boolean confirm(String saved) {
        String input = readLine("Use saved ingredient \"" + saved + "\"? (y/n): ").toLowerCase();
        return input.equals("y") || input.equals("yes");
    }

    public static int pickMeal(List<MealSummary> meals) {
        int limit = Math.min(meals.size(), 10);
        System.out.println("\nFound " + meals.size() + " meal(s). Showing top " + limit + ":");
        for (int i = 0; i < limit; i++) {
            System.out.println("  " + (i + 1) + ") " + meals.get(i).name());
        }
        System.out.print("Pick a number (1-" + limit + "): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= limit) {
                return choice - 1;
            }
        } catch (NumberFormatException ignored) {
            // fall through
        }
        System.out.println("Invalid choice — using the first result.");
        return 0;
    }

    public static void display(String recipe) {
        System.out.println("\n----------- Recipe -----------");
        System.out.println(recipe);
        System.out.println("------------------------------");
    }

    public static void info(String message) {
        System.out.println(message);
    }
}
