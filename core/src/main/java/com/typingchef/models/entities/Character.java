package com.typingchef.models.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Character {
    private static final int FRAME_COLS = 22;  // Điều chỉnh số cột trong
    private static final int FRAME_ROWS = 1;   // Điều chỉnh số hàng trong

    private Animation<TextureRegion> walkAnimation;
    private float stateTime;

    private float x, y;
    private float width, height;
    private boolean isMoving;

    private float moveSpeed = 200; // pixels per second
    private float targetX, targetY;

    public interface MovementCompletedCallback {
        void onMovementCompleted(Path completedPath);
    }

    private MovementCompletedCallback callback;
    private Path currentPath;

    public Character(Texture spriteSheet, float startX, float startY) {
        x = startX;
        y = startY;

        createAnimation(spriteSheet);

        this.width = walkAnimation.getKeyFrame(0).getRegionWidth();
        this.height = walkAnimation.getKeyFrame(0).getRegionHeight();

        stateTime = 0f;
    }

    private void createAnimation(Texture spriteSheet) {
        TextureRegion[][] tmp = TextureRegion.split(
            spriteSheet,
            spriteSheet.getWidth() / FRAME_COLS,
            spriteSheet.getHeight() / FRAME_ROWS
        );

        TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }


        walkAnimation = new Animation<>(0.05f, walkFrames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;

        if (isMoving) {
            float dx = targetX - x;
            float dy = targetY - y;
            float distance = (float) Math.sqrt(dx*dx + dy*dy);

            if (distance > 5) {
                float moveDistance = moveSpeed * deltaTime;
                float ratio = moveDistance / distance;

                x += dx * ratio;
                y += dy * ratio;
            } else {
                x = targetX;
                y = targetY;
                isMoving = false;

                if (callback != null && currentPath != null) {
                    callback.onMovementCompleted(currentPath);
                    currentPath = null;
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;

        if (isMoving) {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = walkAnimation.getKeyFrames()[0];
        }

        batch.draw(currentFrame, x, y);
    }

    public void startMovingOnPath(Path path) {
        if (path != null && path.getPoints().size() > 0) {
            int lastIndex = path.getPoints().size() - 1;
            targetX = path.getPoints().get(lastIndex).x;
            targetY = path.getPoints().get(lastIndex).y;
            isMoving = true;
            currentPath = path;
        }
    }

    public void setMovementCompletedCallback(MovementCompletedCallback callback) {
        this.callback = callback;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public boolean isMoving() { return isMoving; }
}
