package io.github.louisnight.turnbasedrpg.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Orc extends Enemy {
    private Vector2 direction;
    private float changeDirectionTimer;
    private float directionDuration;

    public Orc(float x, float y) {
        super(x, y);
        texture = new Texture("../assets/Enemies/Orc-Walk.png");

        boundingBox.setSize(25, 25);

        TextureRegion[][] tmpFrames = TextureRegion.split(texture, 100, 100);
        walkAnimation = new Animation<>(0.1f, tmpFrames[0]);
        position = new Vector2(x, y);
        speed = 50f;
        stateTime = 0f;

        direction = new Vector2(MathUtils.random(-1, 1), MathUtils.random(-1, 1)).nor();
        changeDirectionTimer = 0f;
        directionDuration = MathUtils.random(2f, 5f);
    }

    @Override
    public void update(float delta) {
        changeDirectionTimer += delta;

        if (changeDirectionTimer >= directionDuration) {
            // Pick a new random direction and duration
            direction.set(MathUtils.random(-1, 1), MathUtils.random(-1, 1)).nor();
            directionDuration = MathUtils.random(2f, 5f);
            changeDirectionTimer = 0f;
        }

        // Move in the chosen direction at the set speed
        position.x += direction.x * speed * delta;
        position.y += direction.y * speed * delta;

        updateBoundingBox();
    }

    @Override
    public void render(SpriteBatch batch) {

        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, position.x, position.y);
    }
}
