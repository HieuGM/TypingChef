package com.typingchef.views.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.typingchef.Main;
import com.typingchef.controllers.TypingController;
import com.typingchef.models.entities.ActionType;
import com.typingchef.models.entities.Path;
import com.typingchef.models.systems.StationManager;
import com.typingchef.views.animations.AnimationManager;
import com.typingchef.views.components.WordBubble;
import com.typingchef.models.entities.Character;

public class MainScreen implements Screen {
    private Main game;
    private TiledMap map;
    private OrthographicCamera camera;
    private TiledMapRenderer renderer;
    private SpriteBatch batch;
    private Stage stage;
    private AnimationManager animation;

    private Character chef;
    private Texture chefTexture;

    private StationManager stationManager;
    private TypingController typingController;
    private ShapeRenderer shapeRenderer;

    public MainScreen(Main game) {
        this.game = game;
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
        camera.update();

        batch = game.batch;

        stage = new Stage(new StretchViewport(width, height, camera), batch);

        shapeRenderer = new ShapeRenderer();

        stationManager = new StationManager();
        stationManager.setBreadStationPosition(50, 50);
        stationManager.setCoffeeStationPosition(100, 100);

        typingController = new TypingController(this);

        Gdx.input.setInputProcessor(typingController);

        loadMap();

        loadChef();

        stationManager.activateBreadStation("bread");
        stationManager.activateCoffeeStation("coffee");
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

    public void moveChefToStation(String stationType) {
        float targetX = stationManager.getStationX(stationType);
        float targetY = stationManager.getStationY(stationType);
        moveChefTo(targetX, targetY);
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
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera
        camera.update();

        // Render map
        if (renderer != null) {
            renderer.setView(camera);
            renderer.render();
        }

        // Cập nhật chef và stationManager
        chef.update(delta);
        stationManager.update(delta);

        batch.begin();
        chef.render(batch);
        batch.end();

        stationManager.render(batch, game.font, shapeRenderer);

        stage.act(delta);
        stage.draw();
    }

    public WordBubble processTypedCharacter(char c) {
        return stationManager.processTypedCharacter(c);
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
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        stage.dispose();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * Kiểm tra một từ, để sử dụng sau này
     */
    public boolean checkWord(String word) {
        // Sẽ được mở rộng để kiểm tra từ và di chuyển nhân vật
        System.out.println("Kiểm tra từ: " + word);
        return false;
    }
}
