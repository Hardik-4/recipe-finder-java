package griffith;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Fetches recipes from TheMealDB and caches the last successful result for offline use.
 */
public class RecipeAPI {
    private static final String CACHE = "cache.txt";
    private static final String BASE = "https://www.themealdb.com/api/json/v1/1/";
    private static final int TIMEOUT_MS = 8000;

    public static List<MealSummary> searchByIngredient(String ingredient) {
        List<MealSummary> meals = new ArrayList<>();
        try {
            String url = BASE + "filter.php?i=" + encode(ingredient);
            JSONObject result = getJSON(url);
            if (result == null || result.isNull("meals")) {
                return meals;
            }
            JSONArray arr = result.getJSONArray("meals");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject m = arr.getJSONObject(i);
                meals.add(new MealSummary(m.getString("idMeal"), m.getString("strMeal")));
            }
        } catch (Exception ignored) {
            // network / parse failures return empty list
        }
        return meals;
    }

    public static String fetchById(String id) {
        try {
            JSONObject details = getJSON(BASE + "lookup.php?i=" + encode(id));
            if (details == null || details.isNull("meals")) {
                return null;
            }
            return formatMeal(details.getJSONArray("meals").getJSONObject(0));
        } catch (Exception e) {
            return null;
        }
    }

    public static String fetchRandom() {
        try {
            JSONObject details = getJSON(BASE + "random.php");
            if (details == null || details.isNull("meals")) {
                return null;
            }
            return formatMeal(details.getJSONArray("meals").getJSONObject(0));
        } catch (Exception e) {
            return null;
        }
    }

    public static String fetchFirstForIngredient(String ingredient) {
        List<MealSummary> meals = searchByIngredient(ingredient);
        if (meals.isEmpty()) {
            return null;
        }
        return fetchById(meals.get(0).id());
    }

    public static void cache(String data) {
        try (FileWriter writer = new FileWriter(CACHE)) {
            writer.write(data);
        } catch (IOException e) {
            System.out.println("Failed to save cache.");
        }
    }

    public static String loadCache() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CACHE))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (IOException e) {
            return null;
        }
    }

    private static String formatMeal(JSONObject meal) {
        String name = meal.optString("strMeal", "Unknown");
        String category = meal.optString("strCategory", "");
        String area = meal.optString("strArea", "");
        String instructions = meal.optString("strInstructions", "");
        String youtube = meal.optString("strYoutube", "");

        StringBuilder ingredients = new StringBuilder();
        for (int i = 1; i <= 20; i++) {
            String ing = meal.optString("strIngredient" + i, "").trim();
            String measure = meal.optString("strMeasure" + i, "").trim();
            if (ing.isEmpty() || "null".equalsIgnoreCase(ing)) {
                continue;
            }
            ingredients.append("  - ");
            if (!measure.isEmpty() && !"null".equalsIgnoreCase(measure)) {
                ingredients.append(measure).append(' ');
            }
            ingredients.append(ing).append('\n');
        }

        StringBuilder out = new StringBuilder();
        out.append("Name: ").append(name).append('\n');
        if (!category.isEmpty()) {
            out.append("Category: ").append(category).append('\n');
        }
        if (!area.isEmpty()) {
            out.append("Cuisine: ").append(area).append('\n');
        }
        out.append("\nIngredients:\n").append(ingredients);
        out.append("\nInstructions:\n").append(instructions).append('\n');
        if (!youtube.isEmpty() && !"null".equalsIgnoreCase(youtube)) {
            out.append("\nVideo: ").append(youtube).append('\n');
        }
        return out.toString();
    }

    private static JSONObject getJSON(String urlStr) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) URI.create(urlStr).toURL().openConnection();
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        int code = conn.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            conn.disconnect();
            return null;
        }

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            return new JSONObject(sb.toString());
        } finally {
            conn.disconnect();
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /** Lightweight meal listing entry from filter results. */
    public static final class MealSummary {
        private final String id;
        private final String name;

        public MealSummary(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String id() {
            return id;
        }

        public String name() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
