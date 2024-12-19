package io.github.louisnight.turnbasedrpg.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

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

    protected Vector2 direction;
    protected float changeDirectionTimer;
    protected float directionDuration;

    public Enemy(float x, float y, float maxHealth) {
        this.position = new Vector2(x, y);
        this.boundingBox = new Rectangle(x, y, 50, 50); // Default size
        this.speed = 50f; // Default speed
        this.state = EnemyState.IDLE;
        this.stateTime = 0f;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;

        this.direction = new Vector2(MathUtils.random(-1, 1), MathUtils.random(-1, 1)).nor();
        this.changeDirectionTimer = 0f;
        this.directionDuration = MathUtils.random(2f, 5f);
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
        float hitboxWidth = boundingBox.width;  // Use existing dimensions
        float hitboxHeight = boundingBox.height;

        // Center the hitbox over the enemy sprite
        boundingBox.setPosition(
            position.x + (getWidth() - hitboxWidth) / 2,
            position.y + (getHeight() - hitboxHeight) / 2
        );
    }

    public float getWidth() {
        if (currentAnimation != null) {
            TextureRegion frame = currentAnimation.getKeyFrame(stateTime);
            return frame.getRegionWidth();
        }
        return 0; // Fallback
    }

    public float getHeight() {
        if (currentAnimation != null) {
            TextureRegion frame = currentAnimation.getKeyFrame(stateTime);
            return frame.getRegionHeight();
        }
        return 0; // Fallback
    }

    public Rectangle getHitbox() {
        return boundingBox;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public boolean checkCollisionWith(Rectangle other) {
        return boundingBox.overlaps(other);
    }

    public Vector2 getPosition() {
        return new Vector2(position.x, position.y);
    }


    // Update and Render methods
    public void update(float delta, ArrayList<Rectangle> collisionRectangles) {
        stateTime += delta;

        // Handle non-DEAD states
        if (state != EnemyState.DEAD) {
            if (state == EnemyState.ATTACKING) {
                // Check if attack animation is finished
                if (attackAnimation.isAnimationFinished(stateTime)) {
                    System.out.println("Attack animation finished. Switching to IDLE.");
                    setState(EnemyState.IDLE);
                    stateTime = 0f;
                }
            }

            // Handle movement and collision for IDLE or other active states
            if (state == EnemyState.IDLE) {
                // Update direction
                changeDirectionTimer += delta;
                if (changeDirectionTimer >= directionDuration) {
                    direction = new Vector2(MathUtils.random(-1, 1), MathUtils.random(-1, 1)).nor();
                    directionDuration = MathUtils.random(2f, 5f);
                    changeDirectionTimer = 0f;
                }

                // Save current position for rollback
                float prevX = position.x;
                float prevY = position.y;

                // Update position
                position.x += direction.x * speed * delta;
                position.y += direction.y * speed * delta;
                updateBoundingBox();

                // Handle collision
                for (Rectangle obstacle : collisionRectangles) {
                    if (checkCollisionWith(obstacle)) {
                        position.x = prevX;
                        position.y = prevY;
                        updateBoundingBox();

                        // Change direction after collision
                        direction = new Vector2(MathUtils.random(-1, 1), MathUtils.random(-1, 1)).nor();
                        break;
                    }
                }
            }
        }
    }



    public void render(SpriteBatch batch) {
        if (state == null) {
            System.out.println("Warning: Enemy state is null. Defaulting to IDLE.");
            setState(EnemyState.IDLE);
        }

        // Update currentAnimation based on state
        switch (state) {
            case ATTACKING:
                currentAnimation = attackAnimation;
                break;
            case IDLE:
                currentAnimation = idleAnimation;
                break;
            case DEAD:
                currentAnimation = deathAnimation;
                break;
            default:
                currentAnimation = idleAnimation;
        }

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
