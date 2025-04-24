package com.typingchef.views.animations;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class AnimationManager extends Actor {

    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private TextureRegion currentFrame;
    private Direction currentDirection;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public AnimationManager() {
        AnimationLoader.loadAnimations(); // Load animations only once
        currentAnimation = AnimationLoader.getAnimation("down"); // Default animation
        currentDirection = Direction.DOWN;
        stateTime = 0f;
        setBounds(getX(), getY(), 16, 16);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        currentFrame = currentAnimation.getKeyFrame(stateTime, true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }

    public void moveTo(float x, float y) {
        // Xác định hướng (cần cải tiến logic này)
        if (Math.abs(x - getX()) > Math.abs(y - getY())) {
            currentDirection = (x > getX()) ? Direction.RIGHT : Direction.LEFT;
        } else {
            currentDirection = (y > getY()) ? Direction.UP : Direction.DOWN;
        }

        switch (currentDirection) {
            case DOWN:
                currentAnimation = AnimationLoader.getAnimation("down");
                break;
            case LEFT:
                currentAnimation = AnimationLoader.getAnimation("left");
                break;
            case RIGHT:
                currentAnimation = AnimationLoader.getAnimation("right");
                break;
            case UP:
                currentAnimation = AnimationLoader.getAnimation("up");
                break;
        }

        addAction(Actions.moveTo(x, y, 2f));
    }
}
