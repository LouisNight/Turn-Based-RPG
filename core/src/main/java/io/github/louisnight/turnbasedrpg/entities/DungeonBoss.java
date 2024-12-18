package io.github.louisnight.turnbasedrpg.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class DungeonBoss extends Enemy {

    public Animation<TextureRegion> laserAnimation;  // Laser animation
    public boolean laserActive;                      // Whether the laser is active
    private Vector2 laserOffset;                     // Offset for the laser position
    public float laserStateTime;                     // Timer for the laser animation

    public DungeonBoss(float x, float y) {
        super(x, y, 500f); // Initialize with 500 health
        initializeStats();
        loadCombatAssets();
        laserStateTime = 0f;
        laserActive = false;
    }

    @Override
    protected void initializeStats() {
        this.speed = 0f; // The boss does not move
        this.maxHealth = 500f;
        this.currentHealth = maxHealth;
    }

    @Override
    public void loadCombatAssets() {
        idleAnimation = createAnimation("../assets/Enemies/DungeonBoss/idle1.png", 100, 100, 0.2f, Animation.PlayMode.LOOP);
        attackAnimation = createAnimation("../assets/Enemies/DungeonBoss/realCharge1.png", 100, 100, 0.1f, Animation.PlayMode.NORMAL);
        laserAnimation = createAnimation("../assets/Enemies/DungeonBoss/realLaser1.png", 300, 100, 0.1f, Animation.PlayMode.NORMAL);

    }

    private Animation<TextureRegion> createAnimation(String filePath, int frameWidth, int frameHeight, float frameDuration, Animation.PlayMode playMode) {
        Texture spriteSheet = new Texture(filePath);
        TextureRegion[][] tmpFrames = TextureRegion.split(spriteSheet, frameWidth, frameHeight);
        Array<TextureRegion> animationFrames = new Array<>();

        for (TextureRegion[] row : tmpFrames) {
            for (TextureRegion frame : row) {
                if (frame != null) {
                    animationFrames.add(frame);
                }
            }
        }

        return new Animation<>(frameDuration, animationFrames, playMode);
    }


    @Override
    public void update(float delta, ArrayList<Rectangle> collisionRectangles) {
        stateTime += delta;

        if (state == EnemyState.ATTACKING) {
            // Transition to laser phase after attack animation
            if (attackAnimation.isAnimationFinished(stateTime)) {
                if (!laserActive) {
                    System.out.println("Charge-up animation finished. Activating laser.");
                    laserActive = true;
                    laserStateTime = 0f;
                }
            }

            // End laser phase and return to IDLE
            if (laserActive && laserAnimation.isAnimationFinished(laserStateTime)) {
                System.out.println("Laser animation finished. Returning to IDLE.");
                laserActive = false;
                setState(EnemyState.IDLE);
                stateTime = 0f;
            }
        }

        // Increment laser animation time if active
        if (laserActive) {
            laserStateTime += delta;
        }

        updateBoundingBox();
    }




    @Override
    public void render(SpriteBatch batch) {
        // Render the boss animation
        TextureRegion currentFrame = getCurrentFrame();
        if (currentFrame != null) {
            batch.draw(currentFrame, position.x, position.y);
        }

        // Render the laser animation if active
        if (laserActive) {
            TextureRegion laserFrame = laserAnimation.getKeyFrame(laserStateTime, false);
            if (laserFrame != null) {
                float laserX = position.x; // Adjust as necessary
                float laserY = position.y + 50; // Example offset
                batch.draw(laserFrame, laserX, laserY);
            }
        }
    }



    private TextureRegion getCurrentFrame() {
        switch (state) {
            case ATTACKING:
                if (!attackAnimation.isAnimationFinished(stateTime)) {
                    return attackAnimation.getKeyFrame(stateTime, false);
                }
                break;
            case HURT:
                if (hurtAnimation != null) {
                    return hurtAnimation.getKeyFrame(stateTime, false);
                }
                break;
            case DEAD:
                if (deathAnimation != null) {
                    return deathAnimation.getKeyFrame(stateTime, false);
                }
                break;
            case IDLE:
            default:
                return idleAnimation.getKeyFrame(stateTime, true);
        }
        return null;
    }
}
