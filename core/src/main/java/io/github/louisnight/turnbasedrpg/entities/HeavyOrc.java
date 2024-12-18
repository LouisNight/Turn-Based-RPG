package io.github.louisnight.turnbasedrpg.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class HeavyOrc extends Enemy {
    private Vector2 direction;
    private float changeDirectionTimer;
    private float directionDuration;
    private EnemyState state;

    protected float health;
    protected float maxHealth;

    public HeavyOrc(float x, float y) {
        super(x, y, 120f);
        state = EnemyState.IDLE;
        texture = new Texture("../assets/Enemies/orc3_walk_full.png");
        loadCombatAssets();

        float spriteWidth = 64f; // Assuming each frame is 64x64
        float spriteHeight = 64f;
        boundingBox.setSize(spriteWidth * 0.6f, spriteHeight * 0.6f); // Adjust proportionally

        TextureRegion[][] tmpFrames = TextureRegion.split(texture, 64, 64);
        walkDownAnimation = new Animation<TextureRegion>(0.1f, tmpFrames[0]);
        walkUpAnimation = new Animation<TextureRegion>(0.1f, tmpFrames[1]);
        walkLeftAnimation = new Animation<TextureRegion>(0.1f, tmpFrames[2]);
        walkRightAnimation = new Animation<TextureRegion>(0.1f, tmpFrames[3]);

        currentAnimation = walkDownAnimation;
        position = new Vector2(x, y);
        speed = 50f;
        stateTime = 0f;

        direction = new Vector2(MathUtils.random(-1, 1), MathUtils.random(-1, 1)).nor();
        changeDirectionTimer = 0f;
        directionDuration = MathUtils.random(2f, 5f);



    }

    @Override
    protected void initializeStats() {
        maxHealth = 100f;
        health = maxHealth;
    }

    @Override
    public void loadCombatAssets() {

        // ATTACK ANIMATION
        Texture attackSpriteSheet = new Texture("../assets/Enemies/orc3_attack_full.png");
        int attackFrameCols = 8;
        int attackFrameRows = 4;
        TextureRegion[][] attackTmp = TextureRegion.split(attackSpriteSheet, attackSpriteSheet.getWidth() / attackFrameCols,
            attackSpriteSheet.getHeight() / attackFrameRows);

        TextureRegion[] thirdRowFramesAttack = attackTmp[2];

        Array<TextureRegion> attackFrames = new Array<>(thirdRowFramesAttack);

        attackAnimation = new Animation<>(0.1f, attackFrames, Animation.PlayMode.NORMAL);

        if (attackAnimation == null) {
            System.out.println("Attack animation for HeavyOrc is not initialized!");
        }
        // IDLE ANIMATION
        Texture idleSpriteSheet = new Texture("../assets/Enemies/orc3_idle_full.png");
        int idleFrameCols = 4;
        int idleFrameRows = 4;
        TextureRegion[][] idleTmp = TextureRegion.split(idleSpriteSheet, idleSpriteSheet.getWidth() / idleFrameCols,
            idleSpriteSheet.getHeight() / idleFrameRows);

        TextureRegion[] thirdRowFramesIdle = idleTmp[2];

        Array<TextureRegion> idleFrames = new Array<>(thirdRowFramesIdle);

        idleAnimation = new Animation<>(0.1f, idleFrames, Animation.PlayMode.LOOP);

        // HURT ANIMATION
        Texture hurtSpriteSheet = new Texture("../assets/Enemies/orc3_hurt_full.png");
        int hurtFrameCols = 6;
        int hurtFrameRows = 4;
        TextureRegion[][] hurtTmp = TextureRegion.split(hurtSpriteSheet, hurtSpriteSheet.getWidth() / hurtFrameCols,
            hurtSpriteSheet.getHeight() / hurtFrameRows);

        TextureRegion[] thirdRowFramesHurt = hurtTmp[2];

        Array<TextureRegion> frames = new Array<>(thirdRowFramesHurt);

        hurtAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.NORMAL);


        // DEATH ANIMATION
        Texture deathSpriteSheet = new Texture("../assets/Enemies/orc3_death_full.png");
        int deathFrameCols = 8;
        int deathFrameRows = 4;
        TextureRegion[][] deathTmp = TextureRegion.split(deathSpriteSheet, deathSpriteSheet.getWidth() / deathFrameCols,
            deathSpriteSheet.getHeight() / deathFrameRows);

        TextureRegion[] thirdRowFramesDeath = deathTmp[2];

        Array<TextureRegion> deathFrames = new Array<>(thirdRowFramesDeath);

        deathAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.NORMAL);

        if (idleAnimation == null) {
            System.out.println("Idle animation for Orc is not initialized!");
        } else {
            System.out.println("Idle animation for Orc is successfully initialized.");
        }
    }


    @Override
    public void update(float delta, ArrayList<Rectangle> collisionRectangles) {
        super.update(delta, collisionRectangles); // Call the parent class's update method
    }




    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = null;

        switch (state) {
            case ATTACKING:
                System.out.println("State: ATTACKING");
                currentFrame = attackAnimation != null ? attackAnimation.getKeyFrame(stateTime, true) : null;
                break;
            case HURT:
                System.out.println("State: HURT");
                currentFrame = hurtAnimation != null ? hurtAnimation.getKeyFrame(stateTime, true) : null;
                break;
            case DEAD:
                System.out.println("State: DEAD");
                currentFrame = deathAnimation != null ? deathAnimation.getKeyFrame(stateTime, false) : null;
                break;
            case IDLE:
            default:
                System.out.println("State: IDLE");
                currentFrame = idleAnimation != null ? idleAnimation.getKeyFrame(stateTime, true) : null;
                break;
        }

        if (currentFrame != null) {
            batch.draw(currentFrame, position.x, position.y);
        } else {
            System.out.println("No animation available for current state: " + state);
        }
    }
    }
