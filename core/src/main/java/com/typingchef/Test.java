package com.typingchef;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.typingchef.controllers.GameController;
import com.typingchef.models.entities.Customer;
import com.typingchef.models.entities.GameSession;
import com.typingchef.models.systems.ActionStation;


/**
 * Lớp test đơn giản cho mô hình game
 */
public class Test extends ApplicationAdapter {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    private GameSession gameState;
    private GameController controller;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.5f);

        // Khởi tạo game state với level 1
        gameState = new GameSession(1);
        controller = new GameController(gameState);

        // Đăng ký input processor
        Gdx.input.setInputProcessor(new SimpleInputProcessor(controller));
    }

    @Override
    public void render() {
        // Cập nhật game
        controller.update(Gdx.graphics.getDeltaTime());

        // Xóa màn hình
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.3f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Vẽ các trạm từ
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (ActionStation station : gameState.getWordStations()) {
            // Chọn màu dựa trên loại hành động
            switch (station.getActionType()) {
                case PREPARE_BREAD:
                    shapeRenderer.setColor(0.8f, 0.6f, 0.3f, 1f); // Nâu
                    break;
                case PREPARE_COFFEE:
                    shapeRenderer.setColor(0.5f, 0.3f, 0.1f, 1f); // Nâu đậm
                    break;
                case SERVE_CUSTOMER:
                    shapeRenderer.setColor(0.2f, 0.7f, 0.3f, 1f); // Xanh lá
                    break;
                default:
                    shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1f); // Xám
                    break;
            }

            shapeRenderer.rect(station.getX(), station.getY(),
                station.getWidth(), station.getHeight());

            // Vẽ thanh thời gian cho từ nếu có
            if (station.hasWord()) {
                float timePercent = station.getCurrentWord().getTimePercentage();

                // Màu thanh thời gian (xanh -> đỏ khi sắp hết thời gian)
                shapeRenderer.setColor(
                    1 - timePercent,  // Red component
                    timePercent,      // Green component
                    0.2f,             // Blue component
                    1f
                );

                // Vẽ thanh thời gian
                shapeRenderer.rect(
                    station.getX(),
                    station.getY() - 10,
                    station.getWidth() * timePercent,
                    5
                );
            }
        }

        shapeRenderer.end();

        // Vẽ text
        batch.begin();

        // Thông tin game
        font.setColor(Color.WHITE);
        font.draw(batch, "Level: " + gameState.getLevel() +
            "  Score: " + gameState.getScore(), 20, 580);
        font.draw(batch, "Time: " + (int)gameState.getRemainingTime() + "s", 650, 580);

        // Vẽ các từ tại mỗi trạm
        for (ActionStation station : gameState.getWordStations()) {
            if (station.hasWord()) {
                font.setColor(Color.YELLOW);
                font.draw(batch, station.getCurrentWord().getText(),
                    station.getX() + 10,
                    station.getY() + station.getHeight() / 2 + 5);
            }
        }

        // Input hiện tại
        font.setColor(Color.WHITE);
        font.draw(batch, "Input: " + controller.getCurrentInput(), 300, 25);

        // Thông báo
        Color messageColor = controller.wasLastActionSuccessful() ? Color.CYAN : Color.RED;
        font.setColor(messageColor);
        font.draw(batch, controller.getLastMessage(), 300, 50);

        // Đồ đang cầm
        font.setColor(Color.WHITE);
        StringBuilder holding = new StringBuilder("Holding: ");
        if (gameState.hasBread()) holding.append("Bread ");
        if (gameState.hasCoffee()) holding.append("Coffee");
        if (!gameState.hasBread() && !gameState.hasCoffee()) holding.append("Nothing");
        font.draw(batch, holding.toString(), 20, 50);

        // Vẽ thông tin khách hàng
        font.setColor(Color.WHITE);
        float customerY = 500;
        for (Customer customer : gameState.getCustomers()) {
            // Màu tùy thuộc vào độ kiên nhẫn
            float patience = customer.getPatiencePercent();
            font.setColor(
                1 - patience, // Red increases as patience decreases
                patience,     // Green decreases as patience decreases
                0.2f,         // Blue component
                1f
            );

            String wants = "wants: ";
            if (customer.wantsBread()) wants += "Bread ";
            if (customer.wantsCoffee()) wants += "Coffee";

            String status = customer.isServed() ? " (Served)" :
                " (Waiting: " + (int)(patience * 100) + "%)";

            font.draw(batch, "Customer " + customer.getId() + " " + wants + status,
                20, customerY);
            customerY -= 30;
        }

        // Hướng dẫn
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "Type the words and press Enter", 300, 580);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }

    /**
     * Input processor đơn giản
     */
    private static class SimpleInputProcessor extends InputAdapter {
        private GameController controller;

        public SimpleInputProcessor(GameController controller) {
            this.controller = controller;
        }

        @Override
        public boolean keyTyped(char character) {
            controller.handleCharInput(character);
            return true;
        }
    }
}
