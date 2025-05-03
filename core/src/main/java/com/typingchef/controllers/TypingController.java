package com.typingchef.controllers;

import com.badlogic.gdx.InputAdapter;
import com.typingchef.views.components.WordBubble;
import com.typingchef.views.screens.MainScreen;

public class TypingController extends InputAdapter {
    private MainScreen screen;
    private StringBuilder currentInput;

    public TypingController(MainScreen screen) {
        this.screen = screen;
        this.currentInput = new StringBuilder();
    }

    @Override
    public boolean keyTyped(char character) {
        // Backspace
        if (character == '\b') {
            if (currentInput.length() > 0) {
                currentInput.deleteCharAt(currentInput.length() - 1);
            }
            return true;
        }

        // Enter
        if (character == '\n' || character == '\r') {
            checkWord();
            return true;
        }

        // Ký tự thường
        if (Character.isLetterOrDigit(character)) {
            // Thêm ký tự vào input hiện tại
            currentInput.append(character);

            // Kiểm tra với các bong bóng từ
            WordBubble completedBubble = screen.processTypedCharacter(character);

            // Nếu một từ hoàn thành, di chuyển nhân vật
            if (completedBubble != null) {
//                System.out.println("Hoàn thành từ: " + completedBubble.getText() +
//                        " tại trạm " + completedBubble.getStationType());

                // Di chuyển đầu bếp đến trạm tương ứng
                screen.moveChefToStation(completedBubble.getStationType());

                // Reset input sau khi hoàn thành
                currentInput.setLength(0);
            }

            return true;
        }

        return false;
    }

    private void checkWord() {
        String word = currentInput.toString().trim().toLowerCase();
        // Có thể thêm xử lý kiểm tra từ khi nhấn Enter

        // Reset input
        currentInput.setLength(0);
    }

    public String getCurrentInput() {
        return currentInput.toString();
    }
}
