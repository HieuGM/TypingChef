package com.typingchef;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import com.typingchef.views.screens.MainScreen;

public class Main extends ApplicationAdapter {

    private MainScreen mainScreen;

    @Override
    public void create()
    {
        mainScreen = new MainScreen();
    }

    @Override
    public void render()
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mainScreen.render();
    }

    @Override
    public void dispose()
    {
        mainScreen.dispose();
    }

}
