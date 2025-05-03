package com.typingchef.models.entities;

public class Customer {
    private int id;
    private boolean wantsBread;
    private boolean wantsCoffee;
    private float patience;
    private float waitTime;
    private boolean isServed;
    private boolean isHappy;

    public Customer(int id, float patience) {
        this.id = id;
        this.patience = patience;
        this.waitTime = 0;
        this.isServed = false;
        this.isHappy = true;


        this.wantsBread = Math.random() > 0.3;
        this.wantsCoffee = Math.random() > 0.3;

        if (!wantsBread && !wantsCoffee) {
            wantsBread = true;
        }
    }

    public boolean update(float delta) {
        if (isServed) {
            waitTime += delta;
            return waitTime < patience / 2;
        }

        if (!isServed) {
            waitTime += delta;

            if (waitTime >= patience) {
                isHappy = false;
                return false;
            }
        }

        return true;
    }

    public boolean serve(boolean bread, boolean coffee) {
        if (isServed) {
            return false;
        }

        boolean correctOrder =
            (!wantsBread || bread) &&
                (!wantsCoffee || coffee);

        isServed = true;
        isHappy = correctOrder;

        return correctOrder;
    }

    public float getPatiencePercent() {
        return Math.max(0, 1 - (waitTime / patience));
    }

    public int getId() {
        return id;
    }

    public boolean wantsBread() {
        return wantsBread;
    }

    public boolean wantsCoffee() {
        return wantsCoffee;
    }

    public boolean isServed() {
        return isServed;
    }

    public boolean isHappy() {
        return isHappy;
    }

    public float getWaitTime() {
        return waitTime;
    }
}
