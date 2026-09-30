# Recipe Finder (Java)

Console app that finds recipes by ingredient using the free [TheMealDB](https://www.themealdb.com/) API, with local preference storage and offline cache.

## Features

- Search meals by ingredient and pick from the top results
- Fetch a random recipe
- Save last ingredient between runs
- Cache the last recipe for offline viewing
- Shows measures, category, cuisine, and YouTube link when available

## Requirements

- Java 17+
- `lib/json-20250517.jar` (already included)

## Run

```bash
javac -cp "lib/json-20250517.jar" -d out $(find src -name "*.java")
java -cp "out:lib/json-20250517.jar" griffith.Main
```

On Windows (PowerShell):

```powershell
javac -cp "lib\json-20250517.jar" -d out (Get-ChildItem -Recurse src\*.java).FullName
java -cp "out;lib\json-20250517.jar" griffith.Main
```

## Project structure

```
src/griffith/
  Main.java        # menu loop
  RecipeAPI.java   # TheMealDB client + cache
  Settings.java    # saved ingredient
  UI.java          # console I/O
lib/
  json-20250517.jar
documentation.pdf
```

## Author

Hardik Rathee (Hardik-4)
