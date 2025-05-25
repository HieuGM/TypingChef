package com.typingchef.models.systems;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.typingchef.views.components.WordBubble;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StationManager {
    private List<WordBubble> activeBubbles;
    private boolean breadStationActive = false;
    private boolean coffeeStationActive = false;
    private boolean customer1StationActive = false;
    private boolean customer2StationActive = false;

    private float breadStationX = 50;
    private float breadStationY = 50;
    private float coffeeStationX = 50;
    private float coffeeStationY = 150;
    private float customer1StationX = 300;
    private float customer1StationY = 50;
    private float customer2StationX = 300;
    private float customer2StationY = 150;

    private String[] breadWords = {"bread", "toast", "baguette", "roll", "croissant"};
    private String[] coffeeWords = {"fee", "espresso", "latte", "mocha", "mock"};
    private String[] customer1Words = {"order", "please", "serve", "save", "water"};
    private String[] customer2Words = {"check", "chill", "thanks", "delicious", "tasty"};

    public StationManager() {
        activeBubbles = new ArrayList<>();
    }

    /**
     * Kích hoạt trạm bánh mì với từ ngẫu nhiên
     */
    public void activateBreadStation() {
        if (!breadStationActive) {
            int index = (int)(Math.random() * breadWords.length);
            WordBubble bubble = new WordBubble(breadWords[index], breadStationX, breadStationY, "bread");
            activeBubbles.add(bubble);
            breadStationActive = true;
        }
    }

    /**
     * Kích hoạt trạm cà phê với từ ngẫu nhiên
     */
    public void activateCoffeeStation() {
        if (!coffeeStationActive) {
            int index = (int)(Math.random() * coffeeWords.length);
            WordBubble bubble = new WordBubble(coffeeWords[index], coffeeStationX, coffeeStationY, "coffee");
            activeBubbles.add(bubble);
            coffeeStationActive = true;
        }
    }

    public void activateCustomer1Station() {
        if (!customer1StationActive) {
            int index = (int)(Math.random() * customer1Words.length);
            WordBubble bubble = new WordBubble(customer1Words[index], customer1StationX, customer1StationY, "customer1");
            activeBubbles.add(bubble);
            customer1StationActive = true;
        }
    }
    public void activateCustomer2Station() {
        if (!customer2StationActive) {
            int index = (int)(Math.random() * customer2Words.length);
            WordBubble bubble = new WordBubble(customer2Words[index], customer2StationX, customer2StationY, "customer2");
            activeBubbles.add(bubble);
            customer2StationActive = true;
        }
    }
    /**
     * Cập nhật các bong bóng từ hiện có
     */
    public void update(float delta) {
        Iterator<WordBubble> iterator = activeBubbles.iterator();
        while (iterator.hasNext()) {
            WordBubble bubble = iterator.next();
            boolean timeout = bubble.update(delta);

            if (timeout || bubble.isCompleted()) {
                iterator.remove();

                if (bubble.getStationType().equals("bread")) {
                    breadStationActive = false;
                } else if (bubble.getStationType().equals("coffee")) {
                    coffeeStationActive = false;
                } else if (bubble.getStationType().equals("customer1")) {
                    customer1StationActive = false;
                } else if (bubble.getStationType().equals("customer2")) {
                    customer2StationActive = false;
                }
            }
        }
    }

    /**
     * Vẽ tất cả các bong bóng từ
     */
    public void render(SpriteBatch batch, BitmapFont font, ShapeRenderer shapeRenderer) {
        boolean batchWasDrawing = batch.isDrawing();
        if (!batchWasDrawing) {
            batch.begin();
        }

        for (WordBubble bubble : activeBubbles) {
            bubble.render(batch, font, shapeRenderer);
        }

        if (!batchWasDrawing && batch.isDrawing()) {
            batch.end();
        }
    }

    /**
     * Xử lý ký tự được nhập
     */
    public WordBubble processTypedCharacter(char c) {
        for (WordBubble bubble : activeBubbles) {
            if (!bubble.isCompleted() && !bubble.isTimeout()) {
                boolean matched = bubble.typeCharacter(c);
                if (matched && bubble.isCompleted()) {
                    return bubble;
                }
                if (!matched) {
                    return null;
                }
            }
        }
        return null;
    }

    public WordBubble findBubbleStartingWith(char c) {
        for (WordBubble bubble : activeBubbles) {
            if (!bubble.isCompleted() && !bubble.isTimeout()) {
                String bubbleText = bubble.getText();
                if (bubbleText.length() > 0 &&
                        Character.toLowerCase(bubbleText.charAt(0)) == c) {
                    return bubble;
                }
            }
        }
        return null;
    }
    /**
     * Lấy vị trí X của một trạm
     */
    public float getStationX(String stationType) {
        if ("bread".equals(stationType)) {
            return breadStationX;
        } else if ("coffee".equals(stationType)) {
            return coffeeStationX;
        } else if ("customer1".equals(stationType)) {
            return customer1StationX;
        } else if ("customer2".equals(stationType)) {
            return customer2StationX;
        }
        return 0;
    }

    /**
     * Lấy vị trí Y của một trạm
     */
    public float getStationY(String stationType) {
        if ("bread".equals(stationType)) {
            return breadStationY;
        } else if ("coffee".equals(stationType)) {
            return coffeeStationY;
        } else if ("customer1".equals(stationType)) {
            return customer1StationY;
        } else if ("customer2".equals(stationType)) {
            return customer2StationY;
        }
        return 0;
    }

    public void setBreadStationPosition(float x, float y) {
        this.breadStationX = x;
        this.breadStationY = y;

        // Cập nhật vị trí cho các bong bóng hiện có
        for (WordBubble bubble : activeBubbles) {
            if ("bread".equals(bubble.getStationType())) {
                bubble.setPosition(x, y);
            }
        }
    }

    public void setCoffeeStationPosition(float x, float y) {
        this.coffeeStationX = x;
        this.coffeeStationY = y;

        // Cập nhật vị trí cho các bong bóng hiện có
        for (WordBubble bubble : activeBubbles) {
            if ("coffee".equals(bubble.getStationType())) {
                bubble.setPosition(x, y);
            }
        }
    }
    public void setCustomer1StationPosition(float x, float y) {
        this.customer1StationX = x;
        this.customer1StationY = y;
    }

    public void setCustomer2StationPosition(float x, float y) {
        this.customer2StationX = x;
        this.customer2StationY = y;
    }
}
