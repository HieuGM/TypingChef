package com.typingchef.models.entities;

public class Customer {
    private int id;
    private Order order;
    private float patience;
    private float waitTime;
    private boolean isWaiting;
    private boolean isHappy;

    public Customer(int id, Order order, float patience) {
        this.id = id;
        this.order = order;
        this.patience = patience;
        this.waitTime = 0;
        this.isWaiting = true;
        this.isHappy = true;
    }

    public void update(float delta) {
        if (isWaiting && !order.isCompleted()) {
            waitTime += delta;
            if (waitTime >= patience) {
                isHappy = false;
                isWaiting = false;
            }
        }
    }

    public boolean serve(Dish dish) {
        if (!isWaiting) {
            return false;
        }

        boolean correctDish = order.matches(dish);
        if (correctDish) {
            order.complete();
            isWaiting = false;
            isHappy = true;
        } else {
            isHappy = false;
        }

        return correctDish;
    }

    public int getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public float getPatiencePercentage() {
        return Math.max(0, 1 - (waitTime / patience));
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    public boolean isHappy() {
        return isHappy;
    }
}
