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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity {

    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.3f;
    public static MainActivity mainActivity;
    private static final String recipesFile = "recipes.txt";
    private static HashMap<String, List<String>> recipes = new HashMap<>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;

        try {
            recipes = loadRecipes(recipesFile);
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

    public void addFood(List<String> detectedIngredients) throws IOException {
        List<String> possibleRecipes = findRecipes(detectedIngredients);
        TextView recipesTextView = findViewById(R.id.recipesTextView);
        StringBuilder stringBuilder = new StringBuilder();
        for (String recipe : possibleRecipes) {
            stringBuilder.append(recipe).append("\n"); // Aggiungi una nuova riga per ogni ricetta
        }

        recipesTextView.setText(stringBuilder.toString());
    }

    /*public void addFood(String food) throws IOException {
        //passato il result.getTitle()
        List<String> titlesRecipes = recipes.get(food); //x ingred -> titoli ricette
        HashMap<String, String> recipesWithContent = new HashMap<>();
        //titolo ricetta - contenuto
        if(titlesRecipes != null){
            recipesWithContent = getRecipesContent(titlesRecipes);
        }
    }
     */

   /* private HashMap<String, String> getRecipesContent(List<String> titlesRecipes) throws IOException {
        HashMap<String, String> recipesWithContent = new HashMap<>();
        String nameFileRec;
        for(String nameRecipe : titlesRecipes){
            nameFileRec = nameRecipe + ".txt";
            //in questo modo faccio che legga tutto il file con la ricetta
            //(titolo, ingredienti, contenuto)
            //dobbiamo decidere come poi darlo in output e di conseguenza
            //cambiare la lettura dei file con le ricette
            try (BufferedReader reader = new BufferedReader(new FileReader(nameFileRec))) {
                StringBuilder recipeContentBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    recipeContentBuilder.append(line).append("\n");
                }
                recipesWithContent.put(nameRecipe, recipeContentBuilder.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        return recipesWithContent;
    }

    */
   private static List<String> findRecipes(List<String> detectedIngredients) {
       List<String> possibleRecipes = new ArrayList<>();

       // Ordina la lista di ingredienti rilevati per garantire coerenza
       Collections.sort(detectedIngredients);
       String detectedIngredientsKey = String.join(",", detectedIngredients);

       // Cerca nella mappa delle ricette usando la chiave combinata degli ingredienti rilevati
       List<String> matchingRecipes = recipes.get(detectedIngredientsKey);
       if (matchingRecipes != null) {
           possibleRecipes.addAll(matchingRecipes);
       }

       return possibleRecipes;
   }



    private static HashMap<String, List<String>> loadRecipes(String recipesFile) throws IOException {
        HashMap<String, List<String>> recipesMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(recipesFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String[] ingredients = parts[0].trim().split(",");
                String[] recipes = parts[1].trim().split(",");

                if (ingredients.length > 1) {
                    List<String> combinedIngredients = Arrays.asList(ingredients);
                    Collections.sort(combinedIngredients); // Ordina gli ingredienti per garantire coerenza
                    String combinedIngredientsKey = String.join(",", combinedIngredients);

                    // Controlla se la chiave combinata degli ingredienti esiste già nella mappa
                    List<String> ingredientRecipes = recipesMap.get(combinedIngredientsKey);
                    if (ingredientRecipes == null) {
                        ingredientRecipes = new ArrayList<>();
                        recipesMap.put(combinedIngredientsKey, ingredientRecipes);
                    }

                    // Aggiungi le ricette alla lista delle ricette per questa combinazione di ingredienti
                    ingredientRecipes.addAll(Arrays.asList(recipes));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return recipesMap;
    }



}
