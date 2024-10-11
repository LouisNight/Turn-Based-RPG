package io.github.louisnight.turnbasedrpg.entities.Player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public abstract class Player {

    protected Vector2 position;
    protected float speed;
    protected Animation<TextureRegion> walkAnimation;
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

    protected boolean isMoving;
    protected Rectangle boundingBox;

    protected static final int FRAME_WIDTH = 48;
    protected static final int FRAME_HEIGHT = 48;
    protected static final int CHARACTER_WIDTH = 20;
    protected static final int CHARACTER_HEIGHT = 30;

    protected float health;
    protected float maxHealth;

    protected float damage;
    protected float defense;

    // Constructor
    public Player(float x, float y) {

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

//    public void attack(Enemy enemy) {
//        float totalDamage = getDamage();
//        enemy.takeDamage(totalDamage);
//    }

    public void receiveDamage(float damage) {
        float damageTaken = Math.max(0, damage - getDefense());
        health -= damageTaken;
        System.out.println("Player took " + damageTaken + " damage!");
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
    }
}
