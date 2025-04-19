package com.typingchef.models.entities;

public class Ingredient {
    private String name;
    private String state;
    private boolean isProcessed;

    public Ingredient(String name) {
        this.name = name;
        this.state = "raw";
        this.isProcessed = false;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        this.isProcessed = !this.state.equals("raw");
    }

    public boolean isProcessed() {
        return isProcessed;
    }

}
