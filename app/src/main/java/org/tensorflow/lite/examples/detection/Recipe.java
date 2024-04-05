package org.tensorflow.lite.examples.detection;

import java.util.List;

public class Recipe {

    private String name;
    private List<String> ingredients;
    private int minutes;
    private String level;
    private String instructions;
    private String idImage;
    
    public Recipe(String name, List<String> ingredients, int minutes, String level, String instructions, String idImage ) {
        this.name = name;
        this.ingredients = ingredients;
        this.minutes = minutes;
        this.level = level;
        this.instructions = instructions;
        this.idImage = idImage;
    }

    public String getName() {
        return name;
    }
    public List<String> getIngredients() {
        return ingredients;
    }

    public String getIngredientsToString() {
        String result = String.join(", ", ingredients);
        return result;
    }
    public int getMinutes() {
        return minutes;
    }
    public String getLevel() {
        return level;
    }
    public String getInstructions() {
        return instructions;
    }
    public String getIdImage() {
        return idImage;
    }

    public String displayRecipe() {
        String recipeString = "Recipe: "+name+"\nIngredients:\n";
        for (String ingredient : ingredients) {
            recipeString += "- "+ingredient;
        }
        recipeString += "\nPreparation time: "+minutes
                + "\nLevel: "+level
                +"\nInstructions:\n"+instructions;

        return recipeString;
    }
}
