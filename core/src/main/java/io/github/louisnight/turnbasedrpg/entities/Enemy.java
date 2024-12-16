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

    protected Animation<TextureRegion> attackAnimation;
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> hurtAnimation;
    protected Animation<TextureRegion> deathAnimation;

    protected EnemyState state;

    protected float maxHealth;
    protected float currentHealth;

    public Enemy(float x, float y, float maxHealth) {
        this.position = new Vector2(x, y);
        this.boundingBox = new Rectangle(x, y, 50, 50); // Default size
        this.speed = 50f; // Default speed
        this.state = EnemyState.IDLE;
        this.stateTime = 0f;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        initializeStats();
    }

    protected abstract void initializeStats();

    public abstract void loadCombatAssets();

    // Getters and Setters for health
    public float getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(float currentHealth) {
        this.currentHealth = Math.max(0, Math.min(currentHealth, maxHealth));
        if (this.currentHealth <= 0) {
            setState(EnemyState.DEAD);
        }
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = Math.min(this.currentHealth, maxHealth);
    }

    public void setHealth(float health) {
        this.currentHealth = Math.max(0, Math.min(health, maxHealth)); // Ensure health is within bounds
        if (this.currentHealth <= 0) {
            setState(EnemyState.DEAD); // Update state to DEAD if health reaches 0
        } else if (this.currentHealth < maxHealth * 0.5) {
            setState(EnemyState.HURT); // Set state to HURT if health is below 50%
        } else {
            setState(EnemyState.IDLE); // Reset to IDLE if health is above 50%
        }
    }

    // State management
    public EnemyState getState() {
        return state;
    }

    public void setState(EnemyState newState) {
        if (state != newState) {
            state = newState;
            stateTime = 0f; // Reset state time when state changes
        }
    }

    // Animations
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

    // Position and Bounding Box
    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    protected void updateBoundingBox() {
        boundingBox.setPosition(position.x, position.y);
    }

    public Rectangle getHitbox() {
        return boundingBox;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }


    // Update and Render methods
    public abstract void update(float delta);

    public void render(SpriteBatch batch) {
        if (currentAnimation != null) {
            TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, position.x, position.y);
        } else {
            System.out.println("No animation to render for state: " + state);
        }
    }

    // Resource cleanup
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
