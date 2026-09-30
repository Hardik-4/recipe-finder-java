package griffith;

import java.util.List;

import griffith.RecipeAPI.MealSummary;

/**
 * Console recipe finder backed by TheMealDB, with local settings and offline cache.
 */
public class Main {
    public static void main(String[] args) {
        UI.banner();

        boolean running = true;
        while (running) {
            UI.menu();
            String choice = UI.readLine("").trim();
            switch (choice) {
                case "1":
                    searchByIngredient();
                    break;
                case "2":
                    randomRecipe();
                    break;
                case "3":
                    showCache();
                    break;
                case "4":
                    Settings.clear();
                    UI.info("Saved ingredient cleared.");
                    break;
                case "5":
                case "q":
                case "quit":
                case "exit":
                    running = false;
                    break;
                default:
                    UI.info("Please choose 1-5.");
            }
        }
        UI.info("Goodbye!");
    }

    private static void searchByIngredient() {
        String saved = Settings.load();
        String ingredient;
        if (saved != null && UI.confirm(saved)) {
            ingredient = saved;
        } else {
            ingredient = UI.getIngredient();
            if (ingredient.isEmpty()) {
                UI.info("Ingredient cannot be empty.");
                return;
            }
            Settings.save(ingredient);
        }

        UI.info("Searching recipes for \"" + ingredient + "\"...");
        List<MealSummary> meals = RecipeAPI.searchByIngredient(ingredient);
        if (meals.isEmpty()) {
            UI.info("No online results. Trying offline cache...");
            showCache();
            return;
        }

        int index = UI.pickMeal(meals);
        String recipe = RecipeAPI.fetchById(meals.get(index).id());
        if (recipe == null) {
            UI.info("Could not load recipe details. Trying offline cache...");
            showCache();
            return;
        }

        RecipeAPI.cache(recipe);
        UI.display(recipe);
    }

    private static void randomRecipe() {
        UI.info("Fetching a random recipe...");
        String recipe = RecipeAPI.fetchRandom();
        if (recipe == null) {
            UI.info("No internet. Trying offline cache...");
            showCache();
            return;
        }
        RecipeAPI.cache(recipe);
        UI.display(recipe);
    }

    private static void showCache() {
        String offline = RecipeAPI.loadCache();
        if (offline == null || offline.isBlank()) {
            UI.info("No offline data available.");
        } else {
            UI.display(offline);
        }
    }
}
