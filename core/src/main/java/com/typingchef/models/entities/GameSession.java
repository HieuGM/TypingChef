package com.typingchef.models.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameSession {
    private int level;
    private int score;
    private float gameTime;
    private float levelTime;
    private boolean isGameOver;

    private Kitchen kitchen;
    private List<Customer> customers;
    private int maxCustomers;
    private float customerSpawnTime;
    private float timeSinceLastCustomer;

    private Random random;

    public GameSession(int level) {
        this.level = level;
        this.score = 0;
        this.gameTime = 0;
        this.levelTime = 180;
        this.isGameOver = false;

        this.kitchen = new Kitchen();
        this.customers = new ArrayList<>();
        this.maxCustomers = 3 + level;
        this.customerSpawnTime = Math.max(5, 15 - level);
        this.timeSinceLastCustomer = 0;

        this.random = new Random();
    }

    public void update(float delta) {
        if (isGameOver) {
            return;
        }

        gameTime += delta;
        timeSinceLastCustomer += delta;

        if (gameTime >= levelTime) {
            isGameOver = true;
            return;
        }

        for (Customer customer : customers) {
            customer.update(delta);
        }

        customers.removeIf(customer -> !customer.isWaiting());

        if (customers.size() < maxCustomers && timeSinceLastCustomer >= customerSpawnTime) {
            spawnCustomer();
            timeSinceLastCustomer = 0;
        }
    }

    private void spawnCustomer() {
        List<Recipe> recipes = kitchen.getKnownRecipes();
        if (recipes.isEmpty()) {
            return;
        }

        Recipe randomRecipe = recipes.get(random.nextInt(recipes.size()));
        Order order = new Order(randomRecipe);

        float patience = Math.max(30, 60 - level * 5);

        Customer customer = new Customer(customers.size() + 1, order, patience);
        customers.add(customer);
    }

    public void getIngredient(String ingredientName) {
        kitchen.prepareIngredient(ingredientName);
    }

    public void processIngredient(String ingredientName, String action) {
        kitchen.processIngredient(ingredientName, action);
    }

    public Dish createDish() {
        return kitchen.createDish();
    }

    public boolean serveDish() {
        Dish dish = kitchen.serveDish();
        if (dish == null || customers.isEmpty()) {
            return false;
        }

        for (Customer customer : customers) {
            if (customer.getOrder().matches(dish)) {
                boolean success = customer.serve(dish);
                if (success) {
                    int points = 10 + (int)(customer.getPatiencePercentage() * 20);
                    score += points;
                }
                return success;
            }
        }

        return false;
    }

    public int getLevel() {
        return level;
    }

    public int getScore() {
        return score;
    }

    public float getGameTime() {
        return gameTime;
    }

    public float getRemainingTime() {
        return Math.max(0, levelTime - gameTime);
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public Kitchen getKitchen() {
        return kitchen;
    }

    public List<Customer> getCustomers() {
        return customers;
    }
}
