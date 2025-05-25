package com.typingchef.views.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class AnimatedCharacter extends Image {
    private Texture spriteSheet;
    private Animation<TextureRegion>[] animations;
    private SitDirection sitDirection;
    private Animation<TextureRegion> sitRightAnimation;
    private Animation<TextureRegion> sitLeftAnimation;
    private TextureRegion[] sitRightFrames;
    private TextureRegion[] sitLeftFrames;
    private boolean isSitting = false;
    private boolean facingRight = true;
    private float stateTime;
    private Direction currentDirection;
    private final int frameWidth;
    private final int frameHeight;
    private boolean isAnimationCompleted = false;

    public enum SitDirection {
        RIGHT, LEFT
    }

    public enum Direction {
        RIGHT(0),
        BACK(1),
        LEFT(2),
        FRONT(3);

        final int index;
        Direction(int index) {
            this.index = index;
        }
    }

    public AnimatedCharacter(String spriteSheetPath, int frameWidth, int frameHeight,
                             int framesPerDirection, float frameDuration) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight; // Bỏ việc nhân 2
        this.currentDirection = Direction.RIGHT;
        this.stateTime = 0f;

        loadAnimations(spriteSheetPath, framesPerDirection, frameDuration);

        setSize(frameWidth, frameHeight); // Để nguyên chiều cao
    }

    private void loadAnimations(String spriteSheetPath, int framesPerDirection, float frameDuration) {
        spriteSheet = new Texture(spriteSheetPath);
        // Sử dụng frameHeight bình thường
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);

        animations = new Animation[4];
        for (Direction dir : Direction.values()) {
            TextureRegion[] frames = new TextureRegion[framesPerDirection];
            for (int i = 0; i < framesPerDirection; i++) {
                int frameIndex = dir.index * framesPerDirection + i;
                frames[i] = tmp[frameIndex / (spriteSheet.getWidth() / frameWidth)]
                    [frameIndex % (spriteSheet.getWidth() / frameWidth)];
            }
            animations[dir.index] = new Animation<>(frameDuration, frames);
        }

        setDrawable(new TextureRegionDrawable(animations[Direction.RIGHT.index].getKeyFrame(0)));
    }


    public void loadSitAnimation(String sitAnimationPath) {
        try {
            if (!Gdx.files.internal(sitAnimationPath).exists()) {
                System.err.println("Sit animation file not found: " + sitAnimationPath);
                return;
            }

            Texture sitTexture = new Texture(Gdx.files.internal(sitAnimationPath));
            TextureRegion[][] sitFrames = TextureRegion.split(sitTexture, 16, 32);

            sitRightFrames = new TextureRegion[6];
            sitLeftFrames = new TextureRegion[6];

            for (int i = 0; i < 6; i++) {
                sitRightFrames[i] = sitFrames[0][i];
            }
            for (int i = 0; i < 6; i++) {
                sitLeftFrames[i] = sitFrames[0][i + 6];
            }

            sitRightAnimation = new Animation<>(0.1f, sitRightFrames);
            sitLeftAnimation = new Animation<>(0.1f, sitLeftFrames);

        } catch (Exception e) {
            System.err.println("Error loading sit animation from " + sitAnimationPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void sit() {
        try {
            isSitting = true;
            stateTime = 0;
            facingRight = (sitDirection == SitDirection.RIGHT);
        } catch (Exception e) {
            System.err.println("Error in sit(): " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void setSitDirection(SitDirection direction) {
        this.sitDirection = direction;
    }

    public void update(float delta) {
        stateTime += delta;
        TextureRegion currentFrame;

        if (isSitting) {
            Animation<TextureRegion> currentSitAnim =
                sitDirection == SitDirection.RIGHT ? sitRightAnimation : sitLeftAnimation;
            currentFrame = currentSitAnim.getKeyFrame(stateTime, true);
        } else {
            currentFrame = animations[currentDirection.index].getKeyFrame(stateTime, true);
        }

        setDrawable(new TextureRegionDrawable(currentFrame));
    }
    public void updateDirection(Vector2 from, Vector2 to) {
        float dx = to.x - from.x;
        float dy = to.y - from.y;

        // Determine predominant direction based on movement vector
        if (Math.abs(dx) > Math.abs(dy)) {
            // Horizontal movement is greater
            if (dx > 0) {
                currentDirection = Direction.RIGHT;
            } else {
                currentDirection = Direction.LEFT;
            }
        } else {
            // Vertical movement is greater
            if (dy > 0) {
                currentDirection = Direction.BACK;
            } else {
                currentDirection = Direction.FRONT;
            }
        }

        // Reset animation state
        stateTime = 0;
        isSitting = false;
    }
}
