package com.typingchef.controllers;

import com.badlogic.gdx.InputAdapter;
import com.typingchef.models.entities.ActionType;
import com.typingchef.views.screens.MainScreen;

/**
 * Xử lý input gõ từ
 */
public class TypingController extends InputAdapter {
    private MainScreen mainScreen;
    private StringBuilder currentInput;

    // Các từ mẫu để test (trong thực tế sẽ được thay bằng hệ thống từ động)
    private static final String BREAD_WORD = "bread";
    private static final String COFFEE_WORD = "coffee";

    /**
     * Khởi tạo controller
     * @param mainScreen Màn hình chính
     */
    public TypingController(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
        this.currentInput = new StringBuilder();
    }

    @Override
    public boolean keyTyped(char character) {
        // Nếu là backspace, xóa ký tự cuối
        if (character == '\b') {
            if (currentInput.length() > 0) {
                currentInput.deleteCharAt(currentInput.length() - 1);
            }
        }
        // Nếu là enter, kiểm tra từ
        else if (character == '\n' || character == '\r') {
            checkCurrentWord();
        }
        // Nếu là ký tự bình thường, thêm vào input
        else if (Character.isLetterOrDigit(character) || character == ' ') {
            currentInput.append(character);
        }

        return true;
    }

    /**
     * Kiểm tra từ hiện tại
     */
    private void checkCurrentWord() {
        String input = currentInput.toString().trim().toLowerCase();

        if (input.isEmpty()) {
            return;
        }

        // Kiểm tra từ và thực hiện hành động tương ứng
//        if (input.equals(BREAD_WORD)) {
//            mainScreen.moveChefToStation(ActionType.PREPARE_BREAD);
//        }
//        else if (input.equals(COFFEE_WORD)) {
//            mainScreen.moveChefToStation(ActionType.PREPARE_COFFEE);
//        }
        // Có thể thêm các từ khác ở đây

        // Reset input
        currentInput.setLength(0);
    }

    /**
     * Lấy chuỗi người dùng đang nhập
     * @return Chuỗi hiện tại
     */
    public String getCurrentInput() {
        return currentInput.toString();
    }
}
