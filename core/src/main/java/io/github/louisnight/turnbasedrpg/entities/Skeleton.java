package io.github.louisnight.turnbasedrpg.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Skeleton extends Enemy {
    private Vector2 direction;      // Current direction of movement
    private float changeDirectionTimer;  // Timer for changing direction
    private float directionDuration;     // How long the orc moves in one direction

    public Skeleton(float x, float y) {
        super(x, y);
        texture = new Texture("../assets/Enemies/Skeleton_01_White_Walk.png");

        boundingBox.setSize(96, 64);

        TextureRegion[][] tmpFrames = TextureRegion.split(texture, 96, 64);
        walkAnimation = new Animation<TextureRegion>(0.1f, tmpFrames[0]);
        position = new Vector2(x, y);
        speed = 50f;  // Lower speed for slower movement
        stateTime = 0f;

        direction = new Vector2(MathUtils.random(-1, 1), MathUtils.random(-1, 1)).nor();  // Random direction
        changeDirectionTimer = 0f;
        directionDuration = MathUtils.random(2f, 5f);  // Move in the same direction for 2-5 seconds
    }

    @Override
    public void update(float delta) {
        changeDirectionTimer += delta;

        // If the enemy has been moving in the same direction long enough, change direction
        if (changeDirectionTimer >= directionDuration) {
            // Pick a new random direction and duration
            direction.set(MathUtils.random(-1, 1), MathUtils.random(-1, 1)).nor();
            directionDuration = MathUtils.random(2f, 5f);  // New random direction duration
            changeDirectionTimer = 0f;  // Reset timer
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
