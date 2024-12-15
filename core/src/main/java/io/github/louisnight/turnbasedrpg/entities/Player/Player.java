package io.github.louisnight.turnbasedrpg.entities.Player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public abstract class Player {

    protected Vector2 position;
    protected float speed;
    protected float stateTime;
    protected Texture walkDownTexture;
    protected Texture walkUpTexture;
    protected Texture walkLeftTexture;
    protected Texture walkRightTexture;
    protected Animation<TextureRegion> walkDownAnimation;
    protected Animation<TextureRegion> walkUpAnimation;
    protected Animation<TextureRegion> walkLeftAnimation;
    protected Animation<TextureRegion> walkRightAnimation;
    protected Animation<TextureRegion> currentAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> hurtAnimation;
    private Animation<TextureRegion> deathAnimation;
    private String name;

    protected boolean isMoving;
    protected Rectangle boundingBox;
    protected PlayerState state;

    protected static final int FRAME_WIDTH = 48;
    protected static final int FRAME_HEIGHT = 48;
    protected static final int CHARACTER_WIDTH = 16;
    protected static final int CHARACTER_HEIGHT = 16;

    protected float health;
    protected float maxHealth;

    protected float damage;
    protected float defense;

    // Constructor
    public Player(float x, float y, String name) {
        this.name = name;
        state = PlayerState.IDLE;

        walkDownTexture = new Texture("../assets/Player/WarriorDownWalk.png");
        walkUpTexture = new Texture("../assets/Player/WarriorUpWalk.png");
        walkRightTexture = new Texture("../assets/Player/WarriorRightWalk.png");
        walkLeftTexture = new Texture("../assets/Player/WarriorLeftWalk.png");

        TextureRegion[][] downFrames = TextureRegion.split(walkDownTexture, FRAME_WIDTH, FRAME_HEIGHT);
        TextureRegion[][] upFrames = TextureRegion.split(walkUpTexture, FRAME_WIDTH, FRAME_HEIGHT);
        TextureRegion[][] rightFrames = TextureRegion.split(walkRightTexture, FRAME_WIDTH, FRAME_HEIGHT);
        TextureRegion[][] leftFrames = TextureRegion.split(walkLeftTexture, FRAME_WIDTH, FRAME_HEIGHT);

        walkDownAnimation = new Animation<>(0.1f, downFrames[0]);
        walkUpAnimation = new Animation<>(0.1f, upFrames[0]);
        walkRightAnimation = new Animation<>(0.1f, rightFrames[0]);
        walkLeftAnimation = new Animation<>(0.1f, leftFrames[0]);

        currentAnimation = walkDownAnimation;

        position = new Vector2(x, y);
        speed = 100f;
        stateTime = 0f;

        boundingBox = new Rectangle(position.x, position.y, CHARACTER_WIDTH, CHARACTER_HEIGHT);

        maxHealth = 100f;
        health = maxHealth;

        damage = 1f;
        defense = 5f;
    }

    public String getName() {
        return name;
    }

    public void loadCombatAssets() {

        // ATTACK ANIMATION
        Texture attackSpriteSheet = new Texture("../assets/Player/WarriorRightAttack01.png");
        int attackFrameCols = 6;
        TextureRegion[][] attackTmp = TextureRegion.split(attackSpriteSheet, attackSpriteSheet.getWidth() / attackFrameCols, attackSpriteSheet.getHeight());

        TextureRegion[] attackFrames = attackTmp[0];
        Array<TextureRegion> attackFrameArray = new Array<>(attackFrames);

        attackAnimation = new Animation<>(0.1f, attackFrameArray, Animation.PlayMode.NORMAL);
        if (attackAnimation == null) {
            System.out.println("Attack animation failed to load");
        }


        // IDLE ANIMATION
        Texture idleSpriteSheet = new Texture("../assets/Player/WarriorRightIdle.png");
        int idleFrameCols = 5;
        TextureRegion[][] idleTmp = TextureRegion.split(idleSpriteSheet, idleSpriteSheet.getWidth() / idleFrameCols, idleSpriteSheet.getHeight());

        TextureRegion[] idleFrames = idleTmp[0];
        Array<TextureRegion> idleFrameArray = new Array<>(idleFrames);

        idleAnimation = new Animation<>(0.1f, idleFrameArray, Animation.PlayMode.LOOP);
        if (idleAnimation == null) {
            System.out.println("Idle animation failed to load");
        }

        // HURT ANIMATION
        Texture hurtSpriteSheet = new Texture("../assets/Player/WarriorRightHurt.png");
        int hurtFrameCols = 4;
        TextureRegion[][] hurtTmp = TextureRegion.split(hurtSpriteSheet, hurtSpriteSheet.getWidth() / hurtFrameCols, hurtSpriteSheet.getHeight());

        TextureRegion[] hurtFrames = hurtTmp[0];
        Array<TextureRegion> hurtFrameArray = new Array<>(hurtFrames);

        hurtAnimation = new Animation<>(0.1f, hurtFrameArray, Animation.PlayMode.NORMAL);
        if (hurtAnimation == null) {
            System.out.println("Hurt animation failed to load");
        }

        // DEATH ANIMATION
        Texture deathSpriteSheet = new Texture("../assets/Player/WarriorRightDeath.png");
        int deathFrameCols = 5;
        TextureRegion[][] deathTmp = TextureRegion.split(deathSpriteSheet, deathSpriteSheet.getWidth() / deathFrameCols, deathSpriteSheet.getHeight());

        TextureRegion[] deathFrames = deathTmp[0];
        Array<TextureRegion> deathFrameArray = new Array<>(deathFrames);

        deathAnimation = new Animation<>(0.1f, deathFrameArray, Animation.PlayMode.NORMAL);
        if (deathAnimation == null) {
            System.out.println("Death animation failed to load");
        }
    }


    public Animation<TextureRegion> getAttackAnimation() {
        return attackAnimation;
    }

    public Animation<TextureRegion> getIdleAnimation() {
        if (idleAnimation == null) {
            System.out.println("IDLE ANIMATION IS NULL");
        }
            return idleAnimation;
        }

    public Animation<TextureRegion> getHurtAnimation() {
        return hurtAnimation;
    }

    public Animation<TextureRegion> getDeathAnimation() {
        return deathAnimation;
    }

    public Rectangle getHitbox() {
        return boundingBox;
    }

    public PlayerState getState() {
        return state;
    }

    public void setState(PlayerState newState) {
        if (state != newState) {
            state = newState;
            stateTime = 0f;  // Reset stateTime on state change
        }
    }

    public abstract void update(float delta, boolean moveUp, boolean moveDown, boolean moveLeft, boolean moveRight, ArrayList<Rectangle> collisionRectangles);

    public abstract void render(SpriteBatch batch);

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDefense() {
        return defense;
    }

    public void setDefense(float defense) {
        this.defense = defense;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = Math.max(0, Math.min(health, maxHealth));
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
        this.health = Math.min(this.health, maxHealth);
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }


    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void dispose() {
        walkDownTexture.dispose();
        walkUpTexture.dispose();
        walkRightTexture.dispose();
        walkLeftTexture.dispose();

        if (attackAnimation != null) attackAnimation.getKeyFrames()[0].getTexture().dispose();
        if (idleAnimation != null) idleAnimation.getKeyFrames()[0].getTexture().dispose();
        if (hurtAnimation != null) hurtAnimation.getKeyFrames()[0].getTexture().dispose();
        if (deathAnimation != null) deathAnimation.getKeyFrames()[0].getTexture().dispose();
    }
}
