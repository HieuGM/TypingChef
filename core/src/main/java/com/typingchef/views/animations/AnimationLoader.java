package com.typingchef.views.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class AnimationLoader {

    private static final int FRAME_WIDTH = 16;
    private static final int FRAME_HEIGHT = 16;
    private static final float FRAME_DURATION = 0.1f;
    private static Texture spriteSheet;
    private static TextureRegion[][] frames;
    private static HashMap<String, Animation<TextureRegion>> animations = new HashMap<>();

    public static void loadAnimations() {
        spriteSheet = new Texture(Gdx.files.internal("Bob_run_16x16.png"));
        frames = TextureRegion.split(spriteSheet, FRAME_WIDTH, FRAME_HEIGHT);

        animations.put("down", loadAnimationRow(0));
        animations.put("left", loadAnimationRow(1));
        animations.put("right", loadAnimationRow(2));
        animations.put("up", loadAnimationRow(3));
    }

    private static Animation<TextureRegion> loadAnimationRow(int rowIndex) {
        int frameCount = frames[rowIndex].length;
        TextureRegion[] rowFrames = new TextureRegion[frameCount];

        for (int i = 0; i < frameCount; i++) {
            rowFrames[i] = frames[rowIndex][i];
        }

        return new Animation<>(FRAME_DURATION, rowFrames);
    }

    public static Animation<TextureRegion> getAnimation(String key) {
        return animations.get(key);
    }
}
