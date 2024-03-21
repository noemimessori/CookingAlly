package org.tensorflow.lite.examples.detection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.tensorflow.lite.examples.detection.customview.OverlayView;
import org.tensorflow.lite.examples.detection.env.ImageUtils;
import org.tensorflow.lite.examples.detection.env.Logger;
import org.tensorflow.lite.examples.detection.env.Utils;
import org.tensorflow.lite.examples.detection.tflite.Classifier;
import org.tensorflow.lite.examples.detection.tflite.YoloV5Classifier;
import org.tensorflow.lite.examples.detection.tracking.MultiBoxTracker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MainActivity extends Activity {

    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.3f;
    public static MainActivity mainActivity;
    private static final String recipesFile = "recipes.txt";
    private static final String recipesExcelFile = "recipes_total.xlsx";
    private static HashMap<String, List<String>> recipes = new HashMap<>();
    private static HashMap<String, Recipe> recipesByExcel = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;

        try {
            recipes = loadRecipes(recipesFile);
            recipesByExcel = loadRecipesByExcel(recipesExcelFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DetectorActivity.class));
            }
        });
    }

    public static MainActivity getInstance() {
        if (mainActivity == null) {
            mainActivity = new MainActivity();
        }
        return mainActivity;
    }

    //*************************LOGICA RICETTE CON FILE TESTO

    public void addFood(List<String> detectedIngredients) throws IOException {
        List<String> possibleRecipes = findRecipes(detectedIngredients);
        TextView recipesTextView = findViewById(R.id.recipesTextView);
        StringBuilder stringBuilder = new StringBuilder();
        for (String recipe : possibleRecipes) {
            stringBuilder.append(recipe).append("\n"); // Aggiungi una nuova riga per ogni ricetta
        }

        recipesTextView.setText(stringBuilder.toString());
    }

   private static List<String> findRecipes(List<String> detectedIngredients) {
       List<String> possibleRecipes = new ArrayList<>();

       // Ordina la lista di ingredienti rilevati per garantire coerenza
       Collections.sort(detectedIngredients);
       String detectedIngredientsKey = String.join(",", detectedIngredients);
       System.out.println(detectedIngredientsKey);

       // Cerca nella mappa delle ricette usando la chiave combinata degli ingredienti rilevati
       List<String> matchingRecipes = recipes.get(detectedIngredientsKey);
       if (matchingRecipes != null) {
           possibleRecipes.addAll(matchingRecipes);
       } else {
           System.out.println("Nessuna ricetta trovata");
       }

       return possibleRecipes;
   }

    private HashMap<String, List<String>> loadRecipes(String recipesFile) throws IOException {
        HashMap<String, List<String>> recipesMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(recipesFile)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String[] ingredients = parts[0].trim().split(",");
                String[] recipes = parts[1].trim().split(",");

                if (ingredients.length > 0) {
                    List<String> combinedIngredients = Arrays.asList(ingredients);
                    //rimuovo gli spazi in eccesso che verrebbero considerati nella sort
                    for (int i = 0; i < combinedIngredients.size(); i++) {
                        combinedIngredients.set(i, combinedIngredients.get(i).trim());
                    }
                    Collections.sort(combinedIngredients);
                    String combinedIngredientsKey = String.join(",", combinedIngredients);

                    // Controlla se la chiave combinata degli ingredienti esiste già nella mappa
                    List<String> ingredientRecipes = recipesMap.get(combinedIngredientsKey);
                    if (ingredientRecipes == null) {
                        ingredientRecipes = new ArrayList<>();
                        // Aggiungi le ricette alla lista delle ricette per questa combinazione di ingredienti
                        ingredientRecipes.addAll(Arrays.asList(recipes));
                    }
                    recipesMap.put(combinedIngredientsKey, ingredientRecipes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return recipesMap;
    }

    //*************************LOGICA RICETTE CON FILE EXCEL

    public void addFoodByExcel(List<String> detectedIngredients) throws IOException {
        List<Recipe> possibleRecipes = findRecipesByExcel(detectedIngredients);
        TextView recipesTextView = findViewById(R.id.recipesTextView);
        StringBuilder stringBuilder = new StringBuilder();

        for (Recipe recipe : possibleRecipes) {
            stringBuilder.append(recipe).append("\n\n"); // Aggiungi una nuova riga per ogni ricetta
        }
        recipesTextView.setText(stringBuilder.toString());
    }

    private static List<Recipe> findRecipesByExcel(List<String> detectedIngredients) {
        List<Recipe> possibleRecipes = new ArrayList<>();

        for(Recipe recipe : recipesByExcel.values()) {
            if(recipe.getIngredients().equals(detectedIngredients))
                possibleRecipes.add(recipe);
        }

        if (possibleRecipes.isEmpty())
            System.out.println("Nessuna ricetta trovata");

        return possibleRecipes;
    }

    private HashMap<String, Recipe> loadRecipesByExcel(String recipesExcelFile) throws IOException {
        HashMap<String, Recipe> recipesMap = new HashMap<>();

        try {
            FileInputStream inputStream = new FileInputStream(recipesExcelFile); // open file
            Workbook recipesWorkBook = new XSSFWorkbook(inputStream);
            int numberOfSheets = recipesWorkBook.getNumberOfSheets();

            for (int i=0; i < numberOfSheets; i++) {
                Sheet sheet = recipesWorkBook.getSheetAt(i);
                int firstRow = sheet.getFirstRowNum();
                int lastRow = sheet.getLastRowNum();

                for (int index = firstRow; index <= lastRow; index++) { // 1 recipe for each row
                    Row row = sheet.getRow(index);
                    if(isRowEmpty(row))
                        continue;

                    String name = row.getCell(0).getStringCellValue();
                    String ingredientsString = row.getCell(1).getStringCellValue();
                    String timeString = row.getCell(2).getStringCellValue();
                    String level = row.getCell(3).getStringCellValue();
                    String instructions = row.getCell(4).getStringCellValue();

                    List<String> ingredients = Arrays.asList(ingredientsString.split(", "));
                    int time = Integer.valueOf(timeString);
                    Recipe recipe = new Recipe(name, ingredients, time, level, instructions);

                    recipesMap.put(name, recipe);
                }
            }

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return recipesMap;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null)
            return true;

        for (int i = row.getFirstCellNum(); i <= row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null)  //&& cell.getCellType() != CellType.BLANK
                return false;
        }
        return true;
    }

}
