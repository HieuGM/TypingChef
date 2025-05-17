package com.typingchef;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.typingchef.views.screens.MainScreen;

public class Main extends ApplicationAdapter {

    private MainScreen mainScreen;
    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        mainScreen = new MainScreen(this);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mainScreen.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        mainScreen.resize(width, height);
    }

    @Override
    public void dispose() {
        if (mainScreen != null) {
            mainScreen.dispose();
        }
        batch.dispose();
        font.dispose();
    }
}
