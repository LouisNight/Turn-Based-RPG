package io.github.louisnight.turnbasedrpg.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Enemy {
    protected Vector2 position;
    protected Texture texture;
    protected float speed;
    protected float stateTime;
    protected Rectangle boundingBox;
    protected Animation<TextureRegion> walkDownAnimation;
    protected Animation<TextureRegion> walkUpAnimation;
    protected Animation<TextureRegion> walkLeftAnimation;
    protected Animation<TextureRegion> walkRightAnimation;
    protected Animation<TextureRegion> currentAnimation;
    protected Animation<TextureRegion> walkAnimation;

    public Enemy(float x, float y) {
        position = new Vector2(x, y);
        boundingBox = new Rectangle(x, y, 50, 50);
        speed = 50f;
    }

    public abstract void update(float delta);

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    protected void updateBoundingBox() {
        boundingBox.setPosition(position.x, position.y);
    }


    public void dispose() {
            if (texture != null) {
                texture.dispose();
            }
    }
}
