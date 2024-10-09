package io.github.louisnight.turnbasedrpg.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Orc extends Enemy {

    private float moveCooldown = 1f;
    private float moveTimer = 0f;

    public Orc(float x, float y) {
        super(x, y);
        texture = new Texture("../assets/Enemies/Orc-Walk.png");
        if (texture == null) {
            System.err.println("Orc Texture could not be loaded");
        }
        TextureRegion[][] tmpFrames = TextureRegion.split(texture, 100, 100);

        walkAnimation = new Animation<TextureRegion>(0.1f, tmpFrames[0]);
        position = new Vector2(x, y);
        speed = 50f;
        stateTime = 0f;
    }

    @Override
    public void update(float delta) {
        moveTimer += delta;
        if (moveTimer >= moveCooldown) {
            moveTimer = 0f;
            Vector2 movement = new Vector2(MathUtils.random(-1, 1), MathUtils.random(-1, 1)).nor();
            position.x += movement.x * speed * delta;
            position.y += movement.y * speed * delta;
        }
        stateTime += delta;
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, position.x, position.y);
    }
}
