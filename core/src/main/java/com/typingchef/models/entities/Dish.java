package com.typingchef.models.entities;

public class Dish {
    private Recipe recipe;
    private boolean isReady;
    private float preparationTime;

    public Dish(Recipe recipe) {
        this.recipe = recipe;
        this.isReady = false;
        this.preparationTime = 0;
    }

    public void setReady() {
        this.isReady = true;
    }

    public void update(float delta) {
        if (!isReady) {
            preparationTime += delta;
        }
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public boolean isReady() {
        return isReady;
    }

    public float getPreparationTime() {
        return preparationTime;
    }
}
