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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity {

    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.3f;
    public static MainActivity mainActivity;
    private static final String recipesFile = "recipes.txt";
    private static HashMap<String, List<String>> recipes = new HashMap<>();




    public static MainActivity getInstance() {
        return mainActivity;
    }

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

    public void addFood(String food) throws IOException {
        //passato il result.getTitle()
        List<String> titlesRecipes = recipes.get(food); //x ingred -> titoli ricette
        HashMap<String, String> recipesWithContent = new HashMap<>();
        //titolo ricetta - contenuto
        if(titlesRecipes != null){
            recipesWithContent = getRecipesContent(titlesRecipes);
        }

    }

    private HashMap<String, String> getRecipesContent(List<String> titlesRecipes) throws IOException {
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



    private HashMap<String, List<String>> loadRecipes(String recipesFile) throws IOException {
        HashMap<String, List<String>> recipesMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(recipesFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String[] ingredients = parts[0].trim().split(",");
                String recipeName = parts[1].trim();

                for (String ingredient : ingredients) {
                    String ingredientKey = ingredient.trim();

                    List<String> recipes = recipesMap.get(ingredientKey);
                    if (recipes == null) {
                        recipes = new ArrayList<>();
                        recipesMap.put(ingredientKey, recipes);
                    }
                    recipes.add(recipeName); //così mi va ad aggiornare la mappa?
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return recipesMap;
    }


}
