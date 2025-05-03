package com.typingchef.controllers;

import com.badlogic.gdx.InputAdapter;
import com.typingchef.controllers.GameController;

/**
 * Xử lý input từ bàn phím
 */
public class GameInputProcessor extends InputAdapter {
    private GameController controller;

    /**
     * Tạo input processor mới
     * @param controller Controller để xử lý logic game
     */
    public GameInputProcessor(GameController controller) {
        this.controller = controller;
    }

    /**
     * Xử lý khi người dùng gõ một ký tự
     * @param character Ký tự được gõ
     * @return true nếu xử lý input, false nếu không
     */
    @Override
    public boolean keyTyped(char character) {
        controller.handleCharInput(character);
        return true;
    }
}
