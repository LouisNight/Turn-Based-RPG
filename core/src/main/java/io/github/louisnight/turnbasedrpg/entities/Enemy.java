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
    protected float health;
    protected Animation<TextureRegion> attackAnimation;
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> hurtAnimation;
    protected Animation<TextureRegion> deathAnimation;
    protected EnemyState state;

    public abstract void loadCombatAssets();

    public Enemy(float x, float y) {
        position = new Vector2(x, y);
        boundingBox = new Rectangle(x, y, 50, 50);
        speed = 50f;
        state = EnemyState.IDLE;
        stateTime = 0f;
    }

    public EnemyState getState() {
        return state;
    }

    public void setState(EnemyState newState) {
        if (state != newState) {
            state = newState;
            stateTime = 0f;  // Reset state time when switching states
        }
    }

    public Rectangle getHitbox() {
        return boundingBox;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    public Animation<TextureRegion> getAttackAnimation() {
        return attackAnimation;
    }

    public Animation<TextureRegion> getIdleAnimation() {
        if (idleAnimation == null) {
            System.out.println("NO IDLE ANIMATION FOUND");
        }
        return idleAnimation;
    }

    public Animation<TextureRegion> getHurtAnimation() {
        return hurtAnimation;
    }

    public Animation<TextureRegion> getDeathAnimation() {
        return deathAnimation;
    }

    public abstract void update(float delta);

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
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
