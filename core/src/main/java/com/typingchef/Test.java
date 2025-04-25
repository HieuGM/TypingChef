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

        gameState = new GameSession(1);
        controller = new GameController(gameState);

        Gdx.input.setInputProcessor(new SimpleInputProcessor(controller));
    }

    @Override
    public void render() {
        controller.update(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.3f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (ActionStation station : gameState.getWordStations()) {
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

        }

        shapeRenderer.end();
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Level: " + gameState.getLevel() +
            "  Score: " + gameState.getScore(), 20, 580);
        font.draw(batch, "Time: " + (int)gameState.getRemainingTime() + "s", 650, 580);

        for (ActionStation station : gameState.getWordStations()) {
            if (station.hasWord()) {
                font.setColor(Color.YELLOW);
                font.draw(batch, station.getCurrentWord().getText(),
                    station.getX() + 10,
                    station.getY() + station.getHeight() / 2 + 5);
            }
        }

        font.setColor(Color.WHITE);
        font.draw(batch, "Input: " + controller.getCurrentInput(), 300, 25);

        Color messageColor = controller.wasLastActionSuccessful() ? Color.CYAN : Color.RED;
        font.setColor(messageColor);
        font.draw(batch, controller.getLastMessage(), 300, 50);

        font.setColor(Color.WHITE);
        StringBuilder holding = new StringBuilder("Holding: ");
        if (gameState.hasBread()) holding.append("Bread ");
        if (gameState.hasCoffee()) holding.append("Coffee");
        if (!gameState.hasBread() && !gameState.hasCoffee()) holding.append("Nothing");
        font.draw(batch, holding.toString(), 20, 50);

        font.setColor(Color.WHITE);
        float customerY = 500;
        for (Customer customer : gameState.getCustomers()) {
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
