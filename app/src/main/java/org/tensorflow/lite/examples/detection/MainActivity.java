package org.tensorflow.lite.examples.detection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MainActivity extends Activity {

    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.3f;
    public static MainActivity mainActivity;
    private static final String recipesExcelFile = "recipes_total.xlsx";
    private static HashMap<String, Recipe> recipesByExcel = new HashMap<>();

    public static MainActivity getInstance() {
        if (mainActivity == null) {
            mainActivity = new MainActivity();
        }
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mainActivity = this;

        try {
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

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ArrayList<String> detectedIngredients = intent.getStringArrayListExtra("detectedIngredients");
        ArrayList<Recipe> possibleRecipes = findRecipesByExcel(detectedIngredients);
        LinearLayout recipesLayout = (LinearLayout) findViewById(R.id.RecipeLayout);

        if (recipesLayout.getChildCount() > 0){
            recipesLayout.removeAllViews();
        }

        for(Recipe r : possibleRecipes) {
            LinearLayout lr = new LinearLayout(this);
            lr.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            lr.setOrientation(LinearLayout.HORIZONTAL);

            TextView vr = new TextView(this);
            vr.setText(r.getName());
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            textParams.weight = 1;
            vr.setLayoutParams(textParams);
            lr.addView(vr);

            Button br = new Button(this);
            br.setText("view");
            br.setBackgroundColor(getResources().getColor(R.color.light));
            br.setTextColor(getResources().getColor(R.color.salvia_dark));
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            br.setLayoutParams(buttonParams);
            br.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Intent intent1 = new Intent(MainActivity.this, VisualizzaRicettaActivity.class);
                      intent1.putExtra("recipe", r);
                      startActivity(intent1);
                  }
            });
            lr.addView(br);
            recipesLayout.addView(lr);
        }
    }

    public void addFoodByExcel(ArrayList<String> detectedIngredients) throws IOException {
        TextView noIngredients = findViewById(R.id.noIngredients);
        ArrayList<Recipe> possibleRecipes = findRecipesByExcel(detectedIngredients);

        if (detectedIngredients.isEmpty()) {
            noIngredients.setText("No ingredients detected!");
        }
        else if (possibleRecipes.isEmpty()) {
            noIngredients.setText("No recipes found!");
        } else {
            noIngredients.setText("");
        }
        TableLayout t1 = (TableLayout) findViewById(R.id.tablelayout);
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

        TextView time = new TextView(this);
        TextView ingredients = new TextView(this);
        TextView recipes = new TextView(this);
        long tsLong = (long) (System.currentTimeMillis() / 1000);
        java.util.Date d = new java.util.Date(tsLong * 1000L);
        String ts = new SimpleDateFormat("h:mm a").format(d);
        time.setText(ts);

        StringBuilder builder = new StringBuilder();
        if (detectedIngredients.isEmpty())
            ingredients.setText("not found");
        else {
            for (int i = 0; i < detectedIngredients.size(); i++) {
                builder.append(detectedIngredients.get(i));
                if (i < detectedIngredients.size() - 1) {
                    builder.append(", ");
                }
            }
            ingredients.setText(builder.toString());
        }

        builder = new StringBuilder();
        if (possibleRecipes.isEmpty()) {
            recipes.setText("not found");
        } else {
            for (int i = 0; i < possibleRecipes.size(); i++) {
                builder.append(possibleRecipes.get(i).getName());
                if (i < possibleRecipes.size() - 1) {
                    builder.append(", ");
                }
            }
            recipes.setText(builder.toString());
        }

        time.setGravity(Gravity.CENTER);
        ingredients.setGravity(Gravity.CENTER);
        recipes.setGravity(Gravity.CENTER);
        time.setLayoutParams(new TableRow.LayoutParams(0));
        ingredients.setLayoutParams(new TableRow.LayoutParams(1));
        recipes.setLayoutParams(new TableRow.LayoutParams(2));
        time.getLayoutParams().width = 0;
        ingredients.getLayoutParams().width = 0;
        recipes.getLayoutParams().width = 0;

        tr.addView(time);
        tr.addView(ingredients);
        tr.addView(recipes);
        t1.addView(tr);
    }



    private static ArrayList<Recipe> findRecipesByExcel(ArrayList<String> detectedIngredients) {
        ArrayList<Recipe> possibleRecipes = new ArrayList<>();
        Collections.sort(detectedIngredients);

        for(Recipe recipe : recipesByExcel.values()) {
            System.out.println(detectedIngredients);
            System.out.println(recipe.getIngredients());
            System.out.println(recipe.getIngredients().equals(detectedIngredients));
            if(recipe.getIngredients().equals(detectedIngredients))
                possibleRecipes.add(recipe);
        }
        return possibleRecipes;
    }

    private HashMap<String, Recipe> loadRecipesByExcel(String recipesExcelFile) throws IOException {
        HashMap<String, Recipe> recipesMap = new HashMap<>();

        try {
            InputStream inputStream = getAssets().open(recipesExcelFile);
            XSSFWorkbook recipesWorkBook = new XSSFWorkbook(inputStream);
            int numberOfSheets = recipesWorkBook.getNumberOfSheets();
            Sheet sheet;
            Row row;

            for (int i=0; i < numberOfSheets; i++) {
                sheet = recipesWorkBook.getSheetAt(i);
                int firstRow = sheet.getFirstRowNum();
                int lastRow = sheet.getLastRowNum();

                for (int index = firstRow; index <= lastRow; index++) {
                    row = sheet.getRow(index);
                    if(isRowEmpty(row))
                        continue;

                    String name = row.getCell(0).getStringCellValue();
                    String ingredientsString = row.getCell(1).getStringCellValue();
                    int time = (int) row.getCell(2).getNumericCellValue();
                    String level = row.getCell(3).getStringCellValue();
                    String instructions = row.getCell(4).getStringCellValue();
                    String idImage = row.getCell(5).getStringCellValue();

                    List<String> ingredients = Arrays.asList(ingredientsString.split(", "));
                    Collections.sort(ingredients);
                    Recipe recipe = new Recipe(name, ingredients, time, level, instructions, idImage);
                    System.out.println(recipe.displayRecipe());
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
        if (row.getCell(0) == null)
            return true;
        if (row.getCell(0).getStringCellValue().trim().isEmpty())
            return true;
        return false;
    }





}
