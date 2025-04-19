package com.typingchef.models.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý không gian bếp và quá trình nấu ăn
 */
public class Kitchen {
    private List<Ingredient> availableIngredients; // Nguyên liệu có sẵn
    private List<Ingredient> preparedIngredients;  // Nguyên liệu đã chuẩn bị
    private List<Dish> preparedDishes;             // Món ăn đã hoàn thành
    private List<Recipe> knownRecipes;             // Công thức biết

    public Kitchen() {
        availableIngredients = new ArrayList<>();
        preparedIngredients = new ArrayList<>();
        preparedDishes = new ArrayList<>();
        knownRecipes = new ArrayList<>();

        // Khởi tạo danh sách nguyên liệu có sẵn
        initializeIngredients();
        // Khởi tạo danh sách công thức biết
        initializeRecipes();
    }

    private void initializeIngredients() {
        // Thêm các nguyên liệu cơ bản
        availableIngredients.add(new Ingredient("bread"));
        availableIngredients.add(new Ingredient("meat"));
        availableIngredients.add(new Ingredient("lettuce"));
        availableIngredients.add(new Ingredient("tomato"));
        availableIngredients.add(new Ingredient("cheese"));
    }

    private void initializeRecipes() {
        // Thêm một số công thức cơ bản

        // Hamburger
        Recipe hamburger = new Recipe("Hamburger");
        Ingredient burger_bread = new Ingredient("bread");
        Ingredient burger_meat = new Ingredient("meat");
        burger_meat.setState("cooked");
        hamburger.addIngredient(burger_bread);
        hamburger.addIngredient(burger_meat);
        knownRecipes.add(hamburger);

        // Salad
        Recipe salad = new Recipe("Salad");
        Ingredient salad_lettuce = new Ingredient("lettuce");
        salad_lettuce.setState("chopped");
        Ingredient salad_tomato = new Ingredient("tomato");
        salad_tomato.setState("chopped");
        salad.addIngredient(salad_lettuce);
        salad.addIngredient(salad_tomato);
        knownRecipes.add(salad);

        // Thêm các công thức khác...
    }

    // Chuẩn bị nguyên liệu khi người chơi nhập đúng từ
    public void prepareIngredient(String ingredientName) {
        // Tìm nguyên liệu trong danh sách có sẵn
        for (Ingredient available : availableIngredients) {
            if (available.getName().equals(ingredientName)) {
                // Tạo bản sao để không ảnh hưởng đến nguyên liệu gốc
                Ingredient newIngredient = new Ingredient(ingredientName);
                preparedIngredients.add(newIngredient);
                return;
            }
        }
    }

    // Xử lý nguyên liệu (nấu, cắt, trộn...)
    public void processIngredient(String ingredientName, String action) {
        for (Ingredient ingredient : preparedIngredients) {
            if (ingredient.getName().equals(ingredientName)) {
                ingredient.setState(action);
                return;
            }
        }
    }

    // Tạo món ăn từ nguyên liệu đã chuẩn bị
    public Dish createDish() {
        for (Recipe recipe : knownRecipes) {
            if (recipe.canCreate(preparedIngredients)) {
                Dish newDish = new Dish(recipe);
                preparedDishes.add(newDish);

                // Loại bỏ nguyên liệu đã sử dụng
                // (Đây là một cách đơn giản, có thể cải tiến sau)
                preparedIngredients.clear();

                return newDish;
            }
        }
        return null; // Không tạo được món ăn nào
    }

    // Phục vụ món ăn cho khách
    public Dish serveDish() {
        if (!preparedDishes.isEmpty()) {
            return preparedDishes.remove(0);
        }
        return null;
    }

    // Getters
    public List<Ingredient> getAvailableIngredients() {
        return availableIngredients;
    }

    public List<Ingredient> getPreparedIngredients() {
        return preparedIngredients;
    }

    public List<Dish> getPreparedDishes() {
        return preparedDishes;
    }

    public List<Recipe> getKnownRecipes() {
        return knownRecipes;
    }
}
