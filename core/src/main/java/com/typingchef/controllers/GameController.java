package com.typingchef.controllers;

import com.typingchef.models.entities.GameSession;

public class GameController {
    private GameSession gameState;
    private StringBuilder currentInput;
    private String lastMessage;
    private boolean lastActionSuccessful;

    public GameController(GameSession gameState) {
        this.gameState = gameState;
        this.currentInput = new StringBuilder();
        this.lastMessage = "Game started! Type the words shown.";
        this.lastActionSuccessful = true;
    }

    public void handleCharInput(char character) {
        if (character == '\b') {
            if (currentInput.length() > 0) {
                currentInput.deleteCharAt(currentInput.length() - 1);
            }
        }
        else if (character == '\n' || character == '\r') {
            checkCurrentWord();
        }
        else if (Character.isLetterOrDigit(character) || character == ' ') {
            currentInput.append(character);
        }
    }

    public void checkCurrentWord() {
        String input = currentInput.toString().trim();

        if (input.isEmpty()) {
            setLastMessage("Please type something!", false);
            return;
        }

        boolean correct = gameState.checkWord(input);

        if (correct) {
            setLastMessage("Correct! " + input, true);
        } else {
            setLastMessage("Wrong! Try again.", false);
        }

        currentInput.setLength(0);
    }

    public void update(float delta) {
        gameState.update(delta);
    }

    private void setLastMessage(String message, boolean success) {
        this.lastMessage = message;
        this.lastActionSuccessful = success;
    }

    public String getCurrentInput() {
        return currentInput.toString();
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public boolean wasLastActionSuccessful() {
        return lastActionSuccessful;
    }

    public GameSession getGameState() {
        return gameState;
    }
}
