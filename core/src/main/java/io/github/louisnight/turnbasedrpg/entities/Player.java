package io.github.louisnight.turnbasedrpg.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Player {

    private Texture texture;
    private Vector2 position;
    private float speed;
    private Animation<TextureRegion> walkAnimation;
    private float stateTime;

    private boolean isMoving;
    private Rectangle boundingBox;

    private static final int FRAME_WIDTH = 100;
    private static final int FRAME_HEIGHT = 100;
    private static final int CHARACTER_WIDTH = 15;
    private static final int CHARACTER_HEIGHT = 15;

    private int health;

    public Player(float x, float y) {
        texture = new Texture("../assets/Player/Player_walking_sprite_sheet.png");

        TextureRegion[][] tmpFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
        walkAnimation = new Animation<TextureRegion>(0.1f, tmpFrames[0]);

        position = new Vector2(x, y);
        speed = 100f;
        stateTime = 0f;

        boundingBox = new Rectangle(position.x, position.y, CHARACTER_WIDTH, CHARACTER_HEIGHT);

        health = 100;
    }

    public void update(float delta, boolean moveUp, boolean moveDown, boolean moveLeft, boolean moveRight, ArrayList<Rectangle> collisionRectangles) {
        isMoving = moveUp || moveDown || moveLeft || moveRight;

        // Calculate movement based on input
        Vector2 movement = new Vector2(0, 0);  // <-- Movement vector to store intended movement
        if (moveUp) {
            movement.y += speed * delta;  // <-- Calculate movement based on speed and delta time
        }
        if (moveDown) {
            movement.y -= speed * delta;
        }
        if (moveLeft) {
            movement.x -= speed * delta;
        }
        if (moveRight) {
            movement.x += speed * delta;
        }

        // Predict the player's future position based on the intended movement
        Rectangle futurePosition = new Rectangle(position.x + movement.x, position.y + movement.y, boundingBox.width, boundingBox.height);

        // Check for collisions before applying movement
        if (!isColliding(futurePosition, collisionRectangles)) {
            // Apply the movement to the player's position only if no collision is detected
            position.add(movement);  // <-- Now we update the position
        }

        // Update the bounding box position after the movement
        float offsetX = (FRAME_WIDTH - CHARACTER_WIDTH) / 2;
        float offsetY = (FRAME_HEIGHT - CHARACTER_HEIGHT) / 2;
        boundingBox.setPosition(position.x + offsetX, position.y + offsetY);

        // Update animation state time based on movement
        if (isMoving) {
            stateTime += delta;
        } else {
            stateTime = 0f;
        }
    }

    public void render(SpriteBatch batch) {
        // Get the current frame of the animation based on stateTime
        TextureRegion currentFrame = isMoving ? walkAnimation.getKeyFrame(stateTime, true) : walkAnimation.getKeyFrame(0);

        batch.draw(currentFrame, position.x, position.y);
    }

    private boolean isColliding(Rectangle futurePosition, ArrayList<Rectangle> collisionRectangles) {
        for (Rectangle rect : collisionRectangles) {
            if (futurePosition.overlaps(rect)) {
                System.out.println("Collision detected at: " + rect);
                return true;
            }
        }
        return false;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public Vector2 getPosition() {
        return position;
    }
    public void dispose() {
        texture.dispose();
    }
}
