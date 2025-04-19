package com.typingchef.models.entities;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private String name;
    private List<Ingredient> ingredients;

    public Recipe(String name) {
        this.name = name;
        this.ingredients = new ArrayList<>();
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    public boolean canCreate(List<Ingredient> availableIngredients) {
        if (availableIngredients.size() < ingredients.size()) {
            return false;
        }
        for (Ingredient required : ingredients) {
            boolean found = false;
            for (Ingredient available : availableIngredients) {
                if (required.getName().equals(available.getName()) && required.getState().equals(available.getState())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }
}
