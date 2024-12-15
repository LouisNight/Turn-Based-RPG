package io.github.louisnight.turnbasedrpg.entities.Player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class ImplementPlayer extends Player {

    public ImplementPlayer(String name, float x, float y) {
        super(x, y, name);
    }

    @Override
    public void update(float delta, boolean moveUp, boolean moveDown, boolean moveLeft, boolean moveRight, ArrayList<Rectangle> collisionRectangles) {
        isMoving = moveUp || moveDown || moveLeft || moveRight;

        // Calculate movement based on input
        Vector2 movement = new Vector2(0, 0);
        if (moveUp) {
            movement.y += speed * delta;
            currentAnimation = walkUpAnimation;
        }
        if (moveDown) {
            movement.y -= speed * delta;
            currentAnimation = walkDownAnimation;
        }
        if (moveLeft) {
            movement.x -= speed * delta;
            currentAnimation = walkLeftAnimation;
        }
        if (moveRight) {
            movement.x += speed * delta;
            currentAnimation = walkRightAnimation;
        }

        // Predict the player's future position based on the intended movement
        Rectangle futurePosition = new Rectangle(position.x + movement.x, position.y + movement.y, boundingBox.width, boundingBox.height);

        // Check for collisions before applying movement
        if (!isColliding(futurePosition, collisionRectangles)) {
            position.add(movement);  // Update the player's position
        }

        // Update the bounding box position after the movement
        float offsetX = (FRAME_WIDTH - CHARACTER_WIDTH) / 2;
        float offsetY = (FRAME_HEIGHT - CHARACTER_HEIGHT) / 2 - 7;
        boundingBox.setPosition(position.x + offsetX, position.y + offsetY);

        // Update animation state time based on movement
        if (isMoving) {
            stateTime += delta;
        } else {
            stateTime = 0f;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // Get the current frame of the animation based on stateTime
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

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
}
