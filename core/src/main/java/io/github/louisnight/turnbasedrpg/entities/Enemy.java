package io.github.louisnight.turnbasedrpg.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public abstract class Enemy {
    protected Vector2 position;
    protected Texture texture;
    protected Animation<TextureRegion> walkAnimation;
    protected float speed;
    protected float stateTime;

    public Enemy(float x, float y) {
        this.position = new Vector2(x, y);
        this.stateTime = 0f;
    }

    public abstract void update(float delta);  // Define in subclasses
    public abstract void render(SpriteBatch batch);  // Define in subclasses

    public Vector2 getPosition() {
        return position;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
