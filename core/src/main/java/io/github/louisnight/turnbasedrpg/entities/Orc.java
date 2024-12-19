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

public class Orc extends Enemy {
    private Vector2 direction;
    private float changeDirectionTimer;
    private float directionDuration;

    public Orc(float x, float y) {
        super(x, y, 100f);

        texture = new Texture("../assets/Enemies/orc1_walk_full.png");

        float spriteWidth = 64f;
        float spriteHeight = 64f;
        boundingBox.setSize(spriteWidth * 0.6f, spriteHeight * 0.6f); // Adjust proportionally

        TextureRegion[][] tmpFrames = TextureRegion.split(texture, 64, 64);
        walkDownAnimation = new Animation<>(0.1f, tmpFrames[0]);
        walkUpAnimation = new Animation<>(0.1f, tmpFrames[1]);
        walkLeftAnimation = new Animation<>(0.1f, tmpFrames[2]);
        walkRightAnimation = new Animation<>(0.1f, tmpFrames[3]);

        currentAnimation = walkDownAnimation;
        position = new Vector2(x, y);
        speed = 50f;
        stateTime = 0f;

        direction = new Vector2(MathUtils.random(-1, 1), MathUtils.random(-1, 1)).nor();
        changeDirectionTimer = 0f;
        directionDuration = MathUtils.random(2f, 5f);

        initializeStats();
        state = EnemyState.IDLE;

        loadCombatAssets();
    }

    @Override
    protected void initializeStats() {
        maxHealth = 100f;
    }

    @Override
    public void loadCombatAssets() {
        // ATTACK ANIMATION
        Texture attackSpriteSheet = new Texture("../assets/Enemies/orc1_attack_full.png");
        int attackFrameCols = 8;
        int attackFrameRows = 4;
        TextureRegion[][] attackTmp = TextureRegion.split(attackSpriteSheet, attackSpriteSheet.getWidth() / attackFrameCols,
            attackSpriteSheet.getHeight() / attackFrameRows);
        TextureRegion[] attackFrames = attackTmp[2];
        attackAnimation = new Animation<>(0.1f, new Array<>(attackFrames), Animation.PlayMode.NORMAL);

        // IDLE ANIMATION
        Texture idleSpriteSheet = new Texture("../assets/Enemies/orc1_idle_full.png");
        int idleFrameCols = 4;
        int idleFrameRows = 4;
        TextureRegion[][] idleTmp = TextureRegion.split(idleSpriteSheet, idleSpriteSheet.getWidth() / idleFrameCols,
            idleSpriteSheet.getHeight() / idleFrameRows);
        TextureRegion[] idleFrames = idleTmp[2];
        idleAnimation = new Animation<>(0.1f, new Array<>(idleFrames), Animation.PlayMode.LOOP);

        // HURT ANIMATION
        Texture hurtSpriteSheet = new Texture("../assets/Enemies/orc1_hurt_full.png");
        int hurtFrameCols = 6;
        int hurtFrameRows = 4;
        TextureRegion[][] hurtTmp = TextureRegion.split(hurtSpriteSheet, hurtSpriteSheet.getWidth() / hurtFrameCols,
            hurtSpriteSheet.getHeight() / hurtFrameRows);
        TextureRegion[] hurtFrames = hurtTmp[2];
        hurtAnimation = new Animation<>(0.1f, new Array<>(hurtFrames), Animation.PlayMode.NORMAL);

        // DEATH ANIMATION
        Texture deathSpriteSheet = new Texture("../assets/Enemies/orc1_death_full.png");
        int deathFrameCols = 8;
        int deathFrameRows = 4;
        TextureRegion[][] deathTmp = TextureRegion.split(deathSpriteSheet, deathSpriteSheet.getWidth() / deathFrameCols,
            deathSpriteSheet.getHeight() / deathFrameRows);
        TextureRegion[] deathFrames = deathTmp[2];
        deathAnimation = new Animation<>(0.1f, new Array<>(deathFrames), Animation.PlayMode.NORMAL);
    }

    @Override
    public void update(float delta, ArrayList<Rectangle> collisionRectangles) {
        super.update(delta, collisionRectangles); // Call the parent class's update method
    }



    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = getCurrentFrame();
        if (currentFrame != null) {
            batch.draw(currentFrame, position.x, position.y);
        } else {
            System.out.println("No frame available for rendering!");
        }
    }

    private TextureRegion getCurrentFrame() {
        switch (state) {
            case ATTACKING:
                return attackAnimation.getKeyFrame(stateTime, false);
            case HURT:
                return hurtAnimation.getKeyFrame(stateTime, false);
            case DEAD:
                return deathAnimation.getKeyFrame(stateTime, false);
            case IDLE:
            default:
                return idleAnimation.getKeyFrame(stateTime, true);
        }
    }
}
