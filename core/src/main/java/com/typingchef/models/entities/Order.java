package com.typingchef.models.entities;

public class Order {
    private Recipe requestedRecipe;
    private float orderTime;
    private boolean isCompleted;

    public Order(Recipe requestedRecipe) {
        this.requestedRecipe = requestedRecipe;
        this.orderTime = 0;
        this.isCompleted = false;
    }

    public boolean matches(Dish dish) {
        return requestedRecipe.getName().equals(dish.getRecipe().getName());
    }

    public void complete() {
        isCompleted = true;
    }

    public void update(float delta) {
        if (!isCompleted) {
            orderTime += delta;
        }
    }

    public Recipe getRequestedRecipe() {
        return requestedRecipe;
    }

    public float getOrderTime() {
        return orderTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }
}
