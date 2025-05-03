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

    private float breadStationX = 150;
    private float breadStationY = 100;
    private float coffeeStationX = 600;
    private float coffeeStationY = 100;

    private String[] breadWords = {"bread", "toast", "baguette", "roll", "croissant"};
    private String[] coffeeWords = {"coffee", "espresso", "latte", "mocha", "cappuccino"};

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

    /**
     * Kích hoạt trạm bánh mì với từ cụ thể
     */
    public void activateBreadStation(String word) {
        if (!breadStationActive) {
            WordBubble bubble = new WordBubble(word, breadStationX, breadStationY, "bread");
            activeBubbles.add(bubble);
            breadStationActive = true;
        }
    }

    /**
     * Kích hoạt trạm cà phê với từ cụ thể
     */
    public void activateCoffeeStation(String word) {
        if (!coffeeStationActive) {
            WordBubble bubble = new WordBubble(word, coffeeStationX, coffeeStationY, "coffee");
            activeBubbles.add(bubble);
            coffeeStationActive = true;
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

    /**
     * Kiểm tra xem một trạm có đang hoạt động không
     */
    public boolean isStationActive(String stationType) {
        if (stationType.equals("bread")) {
            return breadStationActive;
        } else if (stationType.equals("coffee")) {
            return coffeeStationActive;
        }
        return false;
    }

    /**
     * Lấy vị trí X của một trạm
     */
    public float getStationX(String stationType) {
        if (stationType.equals("bread")) {
            return breadStationX;
        } else if (stationType.equals("coffee")) {
            return coffeeStationX;
        }
        return 0;
    }

    /**
     * Lấy vị trí Y của một trạm
     */
    public float getStationY(String stationType) {
        if (stationType.equals("bread")) {
            return breadStationY;
        } else if (stationType.equals("coffee")) {
            return coffeeStationY;
        }
        return 0;
    }

    /**
     * Thiết lập vị trí cho trạm bánh
     */
    public void setBreadStationPosition(float x, float y) {
        this.breadStationX = x;
        this.breadStationY = y;
    }

    /**
     * Thiết lập vị trí cho trạm cà phê
     */
    public void setCoffeeStationPosition(float x, float y) {
        this.coffeeStationX = x;
        this.coffeeStationY = y;
    }
}
