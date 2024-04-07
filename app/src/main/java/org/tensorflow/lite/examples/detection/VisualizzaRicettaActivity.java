package org.tensorflow.lite.examples.detection;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class VisualizzaRicettaActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_recipes_layout);
        Button goBackButton = findViewById(R.id.goBackButton);
        Intent intent = getIntent();
        List<Recipe> possibleRecipes = (ArrayList<Recipe>) intent.getSerializableExtra("possibleRecipes");
        for (Recipe recipe : possibleRecipes) {
            System.out.println("lupo " + recipe.getName());
        }
        TextView nameRecipe = findViewById(R.id.nameRecipe);
        TextView ingredientsRecipe = findViewById(R.id.ingredientsRecipe);
        TextView minutesRecipe = findViewById(R.id.minutesRecipe);
        TextView levelRecipe = findViewById(R.id.levelRecipe);
        TextView procedureRecipe = findViewById(R.id.procedureRecipe);

        for (Recipe recipe : possibleRecipes) {
            nameRecipe.setText(recipe.getName());
            ingredientsRecipe.setText(recipe.getIngredientsToString());
            levelRecipe.setText(recipe.getLevel());
            minutesRecipe.setText(recipe.getMinutes() + " minutes");
            procedureRecipe.setText(recipe.getInstructions());
            displayRecipeImage(recipe);
        }

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chiama finish() per tornare alla MainActivity
                finish();
            }
        });

    }

    private void displayRecipeImage(Recipe recipe) {
        try {
            InputStream stream = getAssets().open(recipe.getIdImage() + ".png");
            Drawable drawable = Drawable.createFromStream(stream, null);
            ImageView imageView = findViewById(R.id.IdImageRecipe);
            imageView.setImageDrawable(drawable);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
