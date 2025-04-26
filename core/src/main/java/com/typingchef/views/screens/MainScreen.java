package com.typingchef.views.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.typingchef.Main;
import com.typingchef.models.entities.ActionType;
import com.typingchef.models.entities.Character;
import com.typingchef.models.entities.Path;
import com.typingchef.views.animations.AnimationManager;

public class MainScreen implements Screen {
    private Main game;
    private TiledMap map;
    private OrthographicCamera camera;
    private TiledMapRenderer renderer;
    private SpriteBatch batch;
    private Stage stage;
    private AnimationManager animation;

    private Texture chefTexture;
    private float chefX, chefY;
    private float targetX, targetY; // Vị trí đích
    private boolean isMoving;       // Đang di chuyển
    private float moveSpeed;        // Tốc độ di chuyển
    private Character chef;

    public MainScreen(Main game) {
        this.game = game;
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
        camera.update();

        batch = game.batch;

        stage = new Stage(new StretchViewport(width, height, camera), batch);

        Gdx.input.setInputProcessor(stage);
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.SPACE) {
                    float randomX = MathUtils.random(100, Gdx.graphics.getWidth() - 100);
                    float randomY = MathUtils.random(100, Gdx.graphics.getHeight() - 100);
                    moveChefTo(randomX, randomY);
                    return true;
                }
                return false;
            }
        });
        loadMap();

        loadChef();
    }

    public void loadMap() {
        try {
            map = new TmxMapLoader().load("map asset/map2.tmx");
            renderer = new OrthogonalTiledMapRenderer(map);
        } catch (Exception e) {
            System.err.println("Error loading map: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadChef() {
        try {
            chefTexture = new Texture(Gdx.files.internal("character_actions/Adam_run_16x16.png"));

            float startX = Gdx.graphics.getWidth() / 2 - 32;
            float startY = Gdx.graphics.getHeight() / 2 - 32;

            chef = new Character(chefTexture, startX, startY);

        } catch (Exception e) {
            System.err.println("Error loading chef texture: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void moveChefTo(float x, float y) {
        Path path = new Path(ActionType.PREPARE_BREAD);
        path.addPoint(chef.getX(), chef.getY());
        path.addPoint(x, y);
        chef.startMovingOnPath(path);
    }

    public void addAnimation() {
        animation = new AnimationManager();
        animation.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        stage.addActor(animation);
    }

    @Override
    public void show() {
        addAnimation();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        if (renderer != null) {
            renderer.setView(camera);
            renderer.render();
        }

        chef.update(delta);

        batch.begin();

        chef.render(batch);

        batch.end();

        stage.act(delta);
        stage.draw();
    }

    private void updateChefPosition(float delta) {
        if (isMoving) {
            float dx = targetX - chefX;
            float dy = targetY - chefY;
            float distance = (float) Math.sqrt(dx*dx + dy*dy);

            if (distance > 5) {
                float moveDistance = moveSpeed * delta;
                float ratio = moveDistance / distance;

                chefX += dx * ratio;
                chefY += dy * ratio;
            } else {
                chefX = targetX;
                chefY = targetY;
                isMoving = false;
                System.out.println("Chef đã đến đích!");

            }
        }
    }

    @Override
    public void resize(int width, int height) {

        stage.getViewport().update(width, height, true);
        camera.position.set(width / 2f, height / 2f, 0);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if (map != null) {
            map.dispose();
        }
        if (chefTexture != null) {
            chefTexture.dispose();
        }
        stage.dispose();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public boolean checkWord(String word) {
        System.out.println("Kiểm tra từ: " + word);

        if (word.equalsIgnoreCase("move")) {
            moveChefTo(300, 200);
            return true;
        }

        return false;
    }
}
