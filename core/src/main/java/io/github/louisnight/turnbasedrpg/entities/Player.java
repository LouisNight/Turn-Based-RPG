package io.github.louisnight.turnbasedrpg.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Player {

    private Texture texture;
    private Vector2 position;
    private float speed;
    private Animation<TextureRegion> walkAnimation;
    private float stateTime;

    private boolean isMoving;

    public Player(float x, float y) {
        texture = new Texture("../assets/Player/Player_walking_sprite_sheet.png");

        TextureRegion[][] tmpFrames = TextureRegion.split(texture, 100, 100);

        walkAnimation = new Animation<TextureRegion>(0.1f, tmpFrames[0]);
        position = new Vector2(x, y);
        speed = 100f;
        stateTime = 0f;

    }
    public void update(float delta, boolean moveUp, boolean moveDown, boolean moveLeft, boolean moveRight) {
        isMoving = moveUp || moveDown || moveLeft || moveRight;

        if (moveUp) {
            position.y += speed * delta;
        }
        if (moveDown) {
            position.y -= speed * delta;
        }
        if (moveLeft) {
            position.x -= speed * delta;
        }
        if (moveRight) {
            position.x += speed * delta;
        }
        if (isMoving) {
            stateTime += delta;
        } else {
            stateTime = 0f;
        }
    }

    public void render(SpriteBatch batch) {
        // Get the current frame of the animation based on stateTime
        TextureRegion currentFrame = isMoving ? walkAnimation.getKeyFrame(stateTime, true) : walkAnimation.getKeyFrame(0);

        batch.draw(currentFrame, position.x, position.y);
    }

    public Vector2 getPosition() {
        return position;
    }
    public void dispose() {
        texture.dispose();
    }
}
