package com.typingchef.models.systems;

import com.typingchef.models.entities.ActionType;
import com.typingchef.models.entities.Word;

public class ActionStation {
    private ActionType actionType;
    private int x, y;
    private int width, height;
    private boolean isActive;
    private Word currentWord;
    private int customerId;

    public ActionStation(ActionType actionType, int x, int y, int width, int height) {
        this.actionType = actionType;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isActive = true;
        this.currentWord = null;
        this.customerId = -1;
    }

    public ActionStation(int x, int y, int width, int height, int customerId) {
        this.actionType = ActionType.SERVE_CUSTOMER;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isActive = true;
        this.currentWord = null;
        this.customerId = customerId;
    }

    public void setWord(Word word) {
        this.currentWord = word;
    }

    public void clearWord() {
        this.currentWord = null;
    }

    public boolean hasWord() {
        return currentWord != null;
    }

    public void update(float delta) {
//        if (currentWord != null) {
//            boolean valid = currentWord.update(delta);
//            if (!valid) {
//                currentWord = null;
//            }
//        }
    }
    public boolean needsWord() {
        return currentWord == null && isActive;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Word getCurrentWord() {
        return currentWord;
    }

    public int getCustomerId() {
        return customerId;
    }
}
