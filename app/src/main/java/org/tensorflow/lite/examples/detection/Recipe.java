package org.tensorflow.lite.examples.detection;

import java.util.List;

public class Recipe {

    private String name;
    private List<String> ingredients;
    private int minutes;
    private String level;
    private String instructions;
    
    public Recipe(String name, List<String> ingredients, int minutes, String level, String instructions ) {
        this.name = name;
        this.ingredients = ingredients;
        this.minutes = minutes;
        this.level = level;
        this.instructions = instructions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
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
