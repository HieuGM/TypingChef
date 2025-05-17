package com.typingchef.views.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
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
import com.typingchef.models.entities.GameState;
import com.typingchef.models.entities.Path;
import com.typingchef.models.systems.StationManager;
import com.typingchef.views.animations.AnimationManager;
import com.typingchef.views.components.WordBubble;
import com.typingchef.models.entities.Character;

// Thêm vào đầu lớp MainScreen

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
    private Viewport viewport;
    // Thêm các biến mới
    public GameState gameState = GameState.READY;
    private int score = 0;
    private float gameTimer = 120; // 2 phút = 120 giây
    private final float GAME_DURATION = 120f;
    private final int WORD_SCORE = 10; // Điểm cho mỗi từ
    private final int VIEWPORT_WIDTH = 400;
    private final int VIEWPORT_HEIGHT = 272;
    // Thêm biến cho hiệu ứng điểm
    private float scoreEffectTimer = 0;
    private int lastScore = 0;

    // Trong phương thức addScore()
    public void addScore() {
        if (gameState == GameState.PLAYING) {
            score += WORD_SCORE;
            scoreEffectTimer = 0.5f; // Hiệu ứng kéo dài 0.5 giây
        }
    }
    // Two characters
    private Image bobActor;
    private Image aliceActor;
    private Texture bobTexture;
    private Texture aliceTexture;

    // Paths
    private ObjectMap<String, Array<Vector2>> allPaths;
    private float baseSpeed = 20f;

    public MainScreen(Main game) {
        this.game = game;
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        viewport = new StretchViewport(400, 272, camera);
        viewport.apply();
        camera.setToOrtho(false, width, height);
        camera.update();

        batch = game.batch;

        stage = new Stage(viewport, batch);

        shapeRenderer = new ShapeRenderer();

        stationManager = new StationManager();
        stationManager.setBreadStationPosition(50, 50);
        stationManager.setCoffeeStationPosition(50, 150);
        stationManager.setCustomer1StationPosition(300, 50);
        stationManager.setCustomer2StationPosition(300, 150);

        typingController = new TypingController(this);

        Gdx.input.setInputProcessor(typingController);

        loadMap();
        loadActorsAndPaths();
        loadChef();
        stationManager.activateCustomer1Station();
        stationManager.activateCustomer2Station();
        stationManager.activateBreadStation();
        stationManager.activateCoffeeStation();

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
    private void loadActorsAndPaths() {
        float tileSize = 16f;
        // Load textures
        bobTexture = new Texture("bob_run.png");
        aliceTexture = new Texture("alice_run.png");

        // Create Image actors
        bobActor = new Image(bobTexture);
        bobActor.setSize(tileSize, tileSize);
        aliceActor = new Image(aliceTexture);
        aliceActor.setSize(tileSize, tileSize);
        stage.addActor(bobActor);
        stage.addActor(aliceActor);

        // Load all paths from Object Layer "path"
        allPaths = loadAllPaths("path");

        // Initialize Bob on "ghegancua"
        if (allPaths.containsKey("ghegancua")) {
            Array<Vector2> bobPath = allPaths.get("ghegancua");
            if (bobPath.size > 0) {
                Vector2 start = bobPath.first();
                bobActor.setPosition(start.x, start.y);
                moveAlongPath(bobActor, bobPath);
            }
        }
        // Initialize Alice on "ghexacua"
        if (allPaths.containsKey("ghexacua")) {
            Array<Vector2> alicePath = allPaths.get("ghexacua");
            if (alicePath.size > 0) {
                Vector2 start = alicePath.first();
                aliceActor.setPosition(start.x, start.y);
                moveAlongPath(aliceActor, alicePath);
            }
        }
    }
    private ObjectMap<String, Array<Vector2>> loadAllPaths(String layerName) {
        ObjectMap<String, Array<Vector2>> paths = new ObjectMap<>();
        MapLayer layer = map.getLayers().get(layerName);
        if (layer != null) {
            for (MapObject obj : layer.getObjects()) {
                if (obj instanceof PolylineMapObject) {
                    String name = obj.getName();
                    float[] verts = ((PolylineMapObject) obj).getPolyline().getTransformedVertices();
                    Array<Vector2> pts = new Array<>();
                    for (int i = 0; i < verts.length; i += 2) {
                        pts.add(new Vector2(verts[i], verts[i + 1]));
                    }
                    if (name != null) paths.put(name, pts);
                }
            }
        }
        return paths;
    }
    private void updateGameState(float delta) {
        switch (gameState) {
            case READY:
                // Bắt đầu chơi khi người dùng gõ phím đầu tiên
                // (sẽ được xử lý trong TypingController)
                break;

            case PLAYING:
                // Giảm thời gian
                gameTimer -= delta;

                // Kiểm tra hết giờ
                if (gameTimer <= 0) {
                    gameTimer = 0;
                    gameState = GameState.GAME_OVER;
                }
                break;

            case GAME_OVER:
                // Dừng trò chơi
                break;
        }
        if (scoreEffectTimer > 0) {
            scoreEffectTimer -= delta;
        }
    }
    /**
     * Render giao diện người dùng
     */
    private void renderUI() {
        // Bắt đầu batch cho UI
        batch.begin();

        // Lưu màu font gốc
        Color originalColor = game.font.getColor();

        switch (gameState) {
            case READY:
                // Hiển thị màn hình bắt đầu
                game.font.setColor(Color.YELLOW);
                game.font.draw(batch, "Type to start!", VIEWPORT_WIDTH / 2 - 150, VIEWPORT_HEIGHT / 2);
                break;

            case PLAYING:
                if (gameTimer <= 10) { // 10 giây cuối
                    float alpha = (float)Math.abs(Math.sin(gameTimer * 5)); // Nhấp nháy
                    game.font.setColor(new Color(1, 0, 0, 0.5f + alpha * 0.5f)); // Đỏ nhấp nháy
                } else if (gameTimer <= 30) { // 30 giây cuối
                    game.font.setColor(Color.ORANGE);
                } else {
                    game.font.setColor(Color.YELLOW);
                }

                game.font.draw(batch, "Time: " + formatTime(gameTimer), 10, VIEWPORT_HEIGHT - 20);
                game.font.setColor(Color.WHITE);
                game.font.draw(batch, "Score: " + score, VIEWPORT_WIDTH - 100, VIEWPORT_HEIGHT - 20);
                break;

            case GAME_OVER:
                batch.end();
                Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(new Color(0, 0, 0, 0.7f));
                shapeRenderer.rect(VIEWPORT_WIDTH / 2 - 200, VIEWPORT_HEIGHT / 2 - 100, 400, 200);
                shapeRenderer.end();

                // Vẽ viền panel
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(Color.YELLOW);
                shapeRenderer.rect(VIEWPORT_WIDTH / 2 - 200, VIEWPORT_HEIGHT / 2 - 100, 400, 200);
                shapeRenderer.end();
                Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
                batch.begin();
                // Hiển thị kết quả
                game.font.setColor(Color.YELLOW);
                game.font.draw(batch, "GAME OVER!", VIEWPORT_WIDTH / 2 - 40, VIEWPORT_HEIGHT / 2 + 40);
                game.font.setColor(Color.WHITE);
                game.font.draw(batch, "Your Score: " + score, VIEWPORT_WIDTH / 2 - 80, VIEWPORT_HEIGHT / 2);
                game.font.draw(batch, "SPACE TO REPLAY", VIEWPORT_WIDTH / 2 - 100, VIEWPORT_HEIGHT / 2 - 40);
                break;
        }

        // Khôi phục màu font
        game.font.setColor(originalColor);
        if (scoreEffectTimer > 0) {
            // Hiển thị điểm vừa thêm với hiệu ứng
            game.font.setColor(Color.GREEN);
            game.font.getData().setScale(1.2f);
            game.font.draw(batch, "+" + WORD_SCORE, VIEWPORT_WIDTH - 60, VIEWPORT_HEIGHT - 50);
            game.font.getData().setScale(1f);
        }
        batch.end();
    }
    private String formatTime(float timeInSeconds) {
        int minutes = (int)(timeInSeconds / 60);
        int seconds = (int)(timeInSeconds % 60);
        return String.format("%d:%02d", minutes, seconds);
    }
    /**
     * Bắt đầu trò chơi
     */
    public void startGame() {
        if (gameState == GameState.READY) {
            gameState = GameState.PLAYING;
            gameTimer = GAME_DURATION;
            score = 0;

            // Khởi tạo lại các trạm nếu cần
            stationManager.activateBreadStation();
            stationManager.activateCoffeeStation();
            stationManager.activateCustomer1Station();
            stationManager.activateCustomer2Station();
        }
    }
    public void resetGame() {
        if (gameState == GameState.GAME_OVER) {
            gameState = GameState.READY;
        }
    }
    private void moveAlongPath(Image actor, Array<Vector2> path) {
        // Create a single sequence of move actions for smooth, concurrent movement
        SequenceAction seq = Actions.sequence();
        float speed = baseSpeed; // pixels per second
        for (int i = 1; i < path.size; i++) {
            Vector2 from = path.get(i - 1);
            Vector2 to = path.get(i);
            float dist = from.dst(to);
            float duration = dist / speed;
            // Use linear interpolation for consistent speed
            seq.addAction(Actions.moveTo(to.x, to.y, duration, Interpolation.linear));
        }
        // Add the combined sequence to actor once
        actor.addAction(seq);
    }
    public void moveChefTo(float x, float y) {
        float viewportWidth = viewport.getWorldWidth();
        float viewportHeight = viewport.getWorldHeight();

        // Điều chỉnh tọa độ của nhân vật khi phóng to hoặc thu nhỏ
        float worldX = x * (viewportWidth / Gdx.graphics.getWidth());
        float worldY = y * (viewportHeight / Gdx.graphics.getHeight());
        Path path = new Path(ActionType.PREPARE_BREAD);
        path.addPoint(chef.getX(), chef.getY());
        path.addPoint(worldX, worldY);
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

        updateGameState(delta);
        // Update camera
        camera.update();

        // Render map
        if (renderer != null) {
            renderer.setView(camera);
            renderer.render();
        }
        if (gameState == GameState.PLAYING) {
            chef.update(delta);
            stationManager.update(delta);
        }
        // Cập nhật chef và stationManager
        chef.update(delta);
        stationManager.update(delta);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        chef.render(batch);
        batch.end();
        shapeRenderer.setProjectionMatrix(camera.combined);
        stationManager.render(batch, game.font, shapeRenderer);
        stationManager.activateCustomer1Station();
        stationManager.activateCustomer2Station();
        stationManager.activateBreadStation();
        stationManager.activateCoffeeStation();
        stage.act(delta);
        stage.draw();
        if (gameState == GameState.PLAYING || gameState == GameState.READY) {
            stationManager.render(batch, game.font, shapeRenderer);
        }

        // Render UI
        renderUI();
    }
    public WordBubble findBubbleStartingWith(char c) {
        // Tìm từ đầu tiên phù hợp
        return stationManager.findBubbleStartingWith(c);
    }
    public WordBubble processTypedCharacter(char c) {
        return stationManager.processTypedCharacter(c);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        camera.position.set(width / 2f, height / 2f, 0);
        camera.update();
        viewport.update(width, height);
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
}
