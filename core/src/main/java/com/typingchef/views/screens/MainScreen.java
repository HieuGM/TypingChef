package com.typingchef.views.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.typingchef.Main;
import com.typingchef.models.entities.ActionType;
import com.typingchef.models.entities.Character;
import com.typingchef.models.entities.Path;
import com.typingchef.views.animations.AnimatedCharacter;


public class MainScreen implements Screen {
    private AnimatedCharacter adam;
    private AnimatedCharacter alice;

    private Main game;
    private TiledMap map;
    private OrthographicCamera camera;
    private Viewport viewport;

    private TiledMapRenderer renderer;
    private SpriteBatch batch;
    private Stage stage;


    private Texture chefTexture;
    private float chefX, chefY;
    private float targetX, targetY; // Vị trí đích
    private boolean isMoving;       // Đang di chuyển
    private float moveSpeed;        // Tốc độ di chuyển
    private Character chef;

    // Two characters
    private Image bobActor;
    private Image aliceActor;
    private Texture bobTexture;
    private Texture aliceTexture;
    private boolean hasReachedChair = false;
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
        stage = new Stage(viewport);

        loadMap();

        loadActorsAndPaths();
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


    private void loadActorsAndPaths() {
        float tileSize = 16f;
        // Tạo nhân vật Adam với 6 frame cho mỗi hướng
        adam = new AnimatedCharacter("character_actions/Adam_run_16x16.png", 16, 32, 6, 0.1f);
        adam.loadSitAnimation("character_actions/Adam_sit3_16x16.png");
        stage.addActor(adam);

        alice = new AnimatedCharacter("character_actions/Amelia_run_16x16.png", 16, 32, 6, 0.1f);
        alice.loadSitAnimation("character_actions/Amelia_sit3_16x16.png");
        stage.addActor(alice);

        // Load all paths from Object Layer "path"
        allPaths = loadAllPaths("path");

        // Di chuyển Adam
        if (allPaths.containsKey("ghegancua")) {
            Array<Vector2> adamPath = allPaths.get("ghegancua");
            if (adamPath.size > 0) {
                Vector2 start = adamPath.first();
                adam.setPosition(start.x, start.y);
                moveAlongPath1(adam, adamPath);
            }
        }

        if (allPaths.containsKey("ghexacua")) {
            Array<Vector2> alicePath = allPaths.get("ghexacua");
            if (alicePath.size > 0) {
                Vector2 start = alicePath.first();
                alice.setPosition(start.x, start.y);
                moveAlongPath1(alice, alicePath);
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

    private void moveAlongPath1(final AnimatedCharacter character, Array<Vector2> path) {
        SequenceAction seq = Actions.sequence();
        float speed = baseSpeed;

        for (int i = 1; i < path.size; i++) {
            final Vector2 from = path.get(i - 1);
            final Vector2 to = path.get(i);
            float dist = from.dst(to);
            float duration = dist / speed;

            // Update movement direction
            RunnableAction updateDirection = Actions.run(() -> {
                character.updateDirection(from, to);
            });

            seq.addAction(updateDirection);
            seq.addAction(Actions.moveTo(to.x, to.y, duration, Interpolation.linear));

            // At the last point, determine sitting direction based on previous position
            if (i == path.size - 1) {
                final int finalI = i;
                seq.addAction(Actions.run(() -> {
                    // Calculate sitting direction based on final movement
                    Vector2 lastMove = new Vector2(path.get(finalI).x - path.get(finalI - 1).x,
                        path.get(finalI).y - path.get(finalI - 1).y);

                    // If moving right or at same position, sit facing right
                    if (lastMove.x >= 0) {
                        character.setSitDirection(AnimatedCharacter.SitDirection.RIGHT);
                    } else {
                        character.setSitDirection(AnimatedCharacter.SitDirection.LEFT);
                    }
                    character.sit();
                }));
            }
        }

        character.addAction(seq);
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



    @Override
    public void show() {
//        addAnimation();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update animations
        adam.update(delta);
        alice.update(delta);

        // Render bình thường
        camera.update();
        if (renderer != null) {
            renderer.setView(camera);
            renderer.render();
        }

        batch.setProjectionMatrix(camera.combined);
        stage.act(delta);
        stage.draw();
    }

//    private void updatePlayerPosition(float delta) {
//        if (currentTargetIndex >= pathPoints.size) return;
//
//        Vector2 target = pathPoints.get(currentTargetIndex);
//        Vector2 direction = target.cpy().sub(playerPosition).nor();
//        float distance = speed * delta;
//
//        if (playerPosition.dst(target) <= distance) {
//            playerPosition.set(target);
//            currentTargetIndex++;
//        } else {
//            playerPosition.mulAdd(direction, distance);
//        }
//    }

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
