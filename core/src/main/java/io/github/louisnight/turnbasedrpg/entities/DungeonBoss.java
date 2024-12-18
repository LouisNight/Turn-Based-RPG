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
        laserOffset = new Vector2(100, 20); // Offset for laser rendering
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
        idleAnimation = createAnimation("../assets/Enemies/DungeonBoss/idle.png", 100, 100, 0.2f, Animation.PlayMode.LOOP);
        attackAnimation = createAnimation("../assets/Enemies/DungeonBoss/armRaiseLaser.png", 100, 100, 0.1f, Animation.PlayMode.NORMAL);
        laserAnimation = createAnimation("../assets/Enemies/DungeonBoss/laser.png", 300, 100, 0.1f, Animation.PlayMode.NORMAL);
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
            System.out.println("Boss is ATTACKING. State time: " + stateTime);

            if (attackAnimation != null && attackAnimation.isAnimationFinished(stateTime)) {
                if (!laserActive) {
                    System.out.println("Arm Raise animation finished. Activating laser.");
                    laserActive = true;
                    laserStateTime = 0f; // Reset laser animation
                }
            }

            if (laserActive && laserAnimation != null && laserAnimation.isAnimationFinished(laserStateTime)) {
                System.out.println("Laser animation finished. Returning to IDLE.");
                laserActive = false;
                state = EnemyState.IDLE; // Transition to IDLE
                stateTime = 0f; // Reset state time
            }
        } else if (state == EnemyState.IDLE) {
            if (currentAnimation != idleAnimation) {
                System.out.println("Switching to idle animation.");
                currentAnimation = idleAnimation;
            }
        }

        // Update laser timer if active
        if (laserActive) {
            laserStateTime += delta;
            System.out.println("Laser animation progress: " + laserStateTime);
        }

        updateBoundingBox();
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = getCurrentFrame();
        if (currentFrame != null) {
            batch.draw(currentFrame, position.x, position.y);
        }

        // Draw the laser animation if active
        if (laserActive) {
            TextureRegion laserFrame = laserAnimation.getKeyFrame(laserStateTime, false);
            batch.draw(laserFrame, position.x + laserOffset.x, position.y + laserOffset.y);
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
