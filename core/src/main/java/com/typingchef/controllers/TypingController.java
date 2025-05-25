package com.typingchef.controllers;

import com.badlogic.gdx.Input;
import com.typingchef.models.entities.GameState;
import com.badlogic.gdx.InputAdapter;
import com.typingchef.views.components.WordBubble;
import com.typingchef.views.screens.MainScreen;

public class TypingController extends InputAdapter {
    private MainScreen screen;
    private StringBuilder currentInput;
    private WordBubble currentBubble;

    public TypingController(MainScreen screen) {
        this.screen = screen;
        this.currentInput = new StringBuilder();
        this.currentBubble = null;
    }

    @Override
    public boolean keyDown(int keycode) {
        // Xử lý phím Space để chơi lại
        if (keycode == Input.Keys.SPACE && screen.gameState == GameState.GAME_OVER) {
            screen.resetGame();
            return true;
        }
        return false;
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
        if (screen.gameState == GameState.READY) {
            screen.startGame();
        }

        // Không xử lý gõ khi game kết thúc
        if (screen.gameState != GameState.PLAYING) {
            return false;
        }

        // Ký tự thường
        if (Character.isLetterOrDigit(character)) {
            // Thêm ký tự vào input hiện tại
            currentInput.append(character);

            // Kiểm tra với các bong bóng từ
            if (currentBubble == null) {
                // Chưa chọn từ, tìm từ bắt đầu bằng ký tự này
                currentBubble = screen.findBubbleStartingWith(character);
                if (currentBubble != null) {
//                    System.out.println("Bắt đầu gõ từ: " + currentBubble.getText());
                    // Đánh dấu tiến độ gõ ký tự đầu tiên
                    currentBubble.typeCharacter(character);
                }
            } else {
                // Đã có từ đang gõ, kiểm tra ký tự tiếp theo
                boolean matched = currentBubble.typeCharacter(character);
                if (!matched) {
//                    System.out.println("Gõ sai ký tự cho từ: " + currentBubble.getText());
                    // Có thể thêm phản hồi âm thanh/hình ảnh khi gõ sai
                } else {
//                    System.out.println("Gõ đúng ký tự tiếp theo");
                }

                // Kiểm tra nếu từ đã hoàn thành
                if (currentBubble.isCompleted()) {
//                    System.out.println("Hoàn thành từ: " + currentBubble.getText());
                    // Di chuyển đầu bếp và cộng điểm
                    screen.moveChefToStation(currentBubble.getStationType());
                    screen.addScore();

                    // Reset để cho phép chọn từ mới
                    currentBubble = null;
                    currentInput.setLength(0);
                }
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

    // Getter cho currentBubble
    public WordBubble getCurrentBubble() {
        return currentBubble;
    }

    // Reset state khi từ hết thời gian
    public void resetCurrentBubble() {
        this.currentBubble = null;
        this.currentInput.setLength(0);
    }
    public String getCurrentInput() {
        return currentInput.toString();
    }
}
