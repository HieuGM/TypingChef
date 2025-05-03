package com.typingchef.views.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class WordBubble {
    // Constants
    private static final float PADDING_X = 10f;
    private static final float PADDING_Y = 8f;
    private static final float POINTER_SIZE = 10f;
    private static final float CORNER_RADIUS = 8f;

    // Properties
    private String text;                // Từ đầy đủ cần gõ
    private StringBuilder typedText;    // Phần đã gõ
    private float x, y;                 // Vị trí hiển thị (tâm của đối tượng cần hiển thị bong bóng)
    private boolean completed;          // Đã gõ xong chưa
    private float timer;                // Đếm thời gian hiển thị
    private float timeLimit;            // Giới hạn thời gian
    private GlyphLayout layout;         // Dùng để tính kích thước text
    private Rectangle bounds;           // Vùng bao của bong bóng
    private String stationType;         // Loại trạm (bread, coffee)

    // Thêm biến cho ký tự sai
    private char wrongChar = 0;         // Ký tự gõ sai
    private float wrongCharTimer = 0;   // Thời gian hiển thị ký tự sai
    private static final float WRONG_CHAR_DISPLAY_TIME = 1.0f; // Hiển thị ký tự sai

    // Colors
    private Color backgroundColor = new Color(0.2f, 0.2f, 0.2f, 0.7f);
    private Color outlineColor = new Color(0.8f, 0.8f, 0.8f, 1f);
    private Color typedColor = Color.GREEN;
    private Color untypedColor = Color.WHITE;
    private Color timeoutColor = Color.RED;
    private Color wrongColor = Color.RED;  // Màu cho ký tự gõ sai

    public WordBubble(String text, float x, float y, String stationType) {
        this.text = text;
        this.typedText = new StringBuilder();
        this.x = x;
        this.y = y;
        this.stationType = stationType;
        this.completed = false;
        this.timer = 0;
        this.timeLimit = 40.0f;
        this.layout = new GlyphLayout();
        this.bounds = new Rectangle();
        calculateBounds(null);
    }

    /**
     * Tính toán kích thước bong bóng dựa trên text
     */
    private void calculateBounds(BitmapFont font) {
        if (font != null) {
            layout.setText(font, text);
        }

        float width = layout.width + PADDING_X * 2;
        float height = layout.height + PADDING_Y * 2;

        width = Math.max(width, 80);
        height = Math.max(height, 40);

        bounds.set(x - width / 2, y + POINTER_SIZE, width, height);
    }

    /**
     * Cập nhật trạng thái của từ
     * @param delta Thời gian trôi qua từ frame trước
     * @return true nếu từ hết hạn thời gian
     */
    public boolean update(float delta) {
        timer += delta;

        if (wrongChar != 0) {
            wrongCharTimer += delta;
            if (wrongCharTimer >= WRONG_CHAR_DISPLAY_TIME) {
                wrongChar = 0;
                wrongCharTimer = 0;
            }
        }

        return timer >= timeLimit;
    }

    /**
     * Render bong bóng hội thoại và text
     */
    public void render(SpriteBatch batch, BitmapFont font, ShapeRenderer shapeRenderer) {
        if (bounds.width == 0) {
            calculateBounds(font);
        }

        boolean batchWasDrawing = batch.isDrawing();
        if (batchWasDrawing) {
            batch.end();
        }

        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(backgroundColor);
        drawBubbleShape(shapeRenderer);

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(outlineColor);
        drawBubbleShape(shapeRenderer);

        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        batch.begin();

        Color originalColor = font.getColor();

        float textX = bounds.x + PADDING_X;
        float textY = bounds.y + bounds.height - PADDING_Y;

        if (typedText.length() > 0) {
            font.setColor(typedColor);
            font.draw(batch, typedText.toString(), textX, textY);

            layout.setText(font, typedText);
            float nextX = textX + layout.width;

            if (typedText.length() < text.length()) {
                // Vẽ ký tự sai nếu có
                if (wrongChar != 0 && wrongCharTimer < WRONG_CHAR_DISPLAY_TIME) {
                    font.setColor(wrongColor);
                    font.draw(batch, String.valueOf(wrongChar), nextX, textY);

                    layout.setText(font, String.valueOf(wrongChar));
                    float afterWrongCharX = nextX + layout.width;

                    if (timer >= timeLimit) {
                        font.setColor(timeoutColor);
                    } else {
                        font.setColor(untypedColor);
                    }

                    if (typedText.length() + 1 < text.length()) {
                        String remainingText = text.substring(typedText.length() + 1);
                        font.draw(batch, remainingText, afterWrongCharX, textY);
                    }
                } else {
                    if (timer >= timeLimit) {
                        font.setColor(timeoutColor);
                    } else {
                        font.setColor(untypedColor);
                    }
                    String remainingText = text.substring(typedText.length());
                    font.draw(batch, remainingText, nextX, textY);
                }
            }
        } else {
            if (wrongChar != 0 && wrongCharTimer < WRONG_CHAR_DISPLAY_TIME) {
                font.setColor(wrongColor);
                font.draw(batch, String.valueOf(wrongChar), textX, textY);

                layout.setText(font, String.valueOf(wrongChar));
                float afterWrongCharX = textX + layout.width;

                if (timer >= timeLimit) {
                    font.setColor(timeoutColor);
                } else {
                    font.setColor(untypedColor);
                }

                if (text.length() > 1) {
                    String remainingText = text.substring(1);
                    font.draw(batch, remainingText, afterWrongCharX, textY);
                }
            } else {
                if (timer >= timeLimit) {
                    font.setColor(timeoutColor);
                } else {
                    font.setColor(untypedColor);
                }
                font.draw(batch, text, textX, textY);
            }
        }

        font.setColor(originalColor);

        if (!batchWasDrawing) {
            batch.end();
        }
    }

    /**
     * Vẽ hình dạng bong bóng chat
     */
    private void drawBubbleShape(ShapeRenderer renderer) {
        renderer.rect(
            bounds.x, bounds.y,
            bounds.width, bounds.height
        );

        float pointerX = x;
        float pointerY = bounds.y;
        renderer.triangle(
            pointerX - POINTER_SIZE/2, pointerY,
            pointerX + POINTER_SIZE/2, pointerY,
            pointerX, pointerY - POINTER_SIZE
        );
    }

    /**
     * Kiểm tra và xử lý một ký tự đã gõ
     * @param c Ký tự vừa gõ
     * @return true nếu ký tự đúng và từ chưa hoàn thành
     */
    public boolean typeCharacter(char c) {
        if (completed || timer >= timeLimit) {
            return false;
        }

        if (typedText.length() < text.length() &&
            text.charAt(typedText.length()) == c) {
            typedText.append(c);

            if (typedText.length() == text.length()) {
                completed = true;
            }
            return true;
        } else {
            wrongChar = c;
            wrongCharTimer = 0;
            return false;
        }
    }

    // Getters
    public boolean isCompleted() { return completed; }
    public boolean isTimeout() { return timer >= timeLimit; }
    public String getText() { return text; }
    public float getX() { return x; }
    public float getY() { return y; }
    public String getStationType() { return stationType; }
}
