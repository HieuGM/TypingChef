package com.typingchef.views.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.typingchef.views.animations.AnimationManager;

public class MainScreen {
    private TiledMap map;
    private OrthographicCamera camera;
    private TiledMapRenderer renderer;
    private SpriteBatch batch;
    private Stage stage;
    private AnimationManager animation;

    public MainScreen() {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
        camera.update();
        batch = new SpriteBatch();
        stage = new Stage(new StretchViewport(width, height, camera), batch);
        loadMap();
        //addAnimation();
    }

    public void loadMap() {
        map = new TmxMapLoader().load(String.valueOf(Gdx.files.internal("map asset/map2.tmx")));
        renderer = new OrthogonalTiledMapRenderer(map);
    }

    public void addAnimation() {
        animation = new AnimationManager();
        animation.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        stage.addActor(animation);
    }

    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        if(renderer!=null) {
            renderer.setView(camera);
            renderer.render();
        }

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        batch.begin();

        batch.end();


    }
    public void dispose() {
        if (map != null) {
            map.dispose();
        }
        batch.dispose();
        stage.dispose();

    }

    public OrthographicCamera getCamera() {
        return camera;
    }

}
