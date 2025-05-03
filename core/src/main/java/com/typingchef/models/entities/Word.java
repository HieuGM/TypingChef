package com.typingchef.models.entities;

public class Word {
    private String text;
    private ActionType actionType;
    private int customerId;
//    private float timeLimit;
//    private float remainingTime;

    public Word(String text, ActionType actionType) {
        this.text = text;
        this.actionType = actionType;
//        this.timeLimit = timeLimit;
//        this.remainingTime = timeLimit;
        this.customerId = -1;
    }

    public Word(String text, int customerId) {
        this.text = text;
        this.actionType = ActionType.SERVE_CUSTOMER;
        this.customerId = customerId;
//        this.timeLimit = timeLimit;
//        this.remainingTime = timeLimit;
    }

//    public boolean update(float delta) {
//        remainingTime -= delta;
//        return remainingTime > 0;
//    }

    public boolean matches(String input) {
        return text.equalsIgnoreCase(input);
    }

    public String getText() {
        return text;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public int getCustomerId() {
        return customerId;
    }

//    public float getTimeLimit() {
//        return timeLimit;
//    }
//
//    public float getRemainingTime() {
//        return remainingTime;
//    }
//
//    public float getTimePercentage() {
//        return Math.max(0, remainingTime / timeLimit);
//    }
}
