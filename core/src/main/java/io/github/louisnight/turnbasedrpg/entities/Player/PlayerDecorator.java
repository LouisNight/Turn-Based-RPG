package io.github.louisnight.turnbasedrpg.entities.Player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public abstract class PlayerDecorator extends Player {

    protected Player decoratedPlayer;  // Reference to the player being decorated

    public PlayerDecorator(Player decoratedPlayer) {
        super(decoratedPlayer.getPosition().x, decoratedPlayer.getPosition().y, decoratedPlayer.getName());
        this.decoratedPlayer = decoratedPlayer;
    }

    @Override
    public float getHealth() {
        return decoratedPlayer.getHealth();
    }

    @Override
    public float getMaxHealth() {
        return decoratedPlayer.getMaxHealth();
    }

    @Override
    public void setHealth(float health) {
        decoratedPlayer.setHealth(health);
    }

    @Override
    public void setMaxHealth(float maxHealth) {
        decoratedPlayer.setMaxHealth(maxHealth);
    }

    @Override
    public void update(float delta, boolean moveUp, boolean moveDown, boolean moveLeft, boolean moveRight, ArrayList<Rectangle> collisionRectangles) {
        decoratedPlayer.update(delta, moveUp, moveDown, moveLeft, moveRight, collisionRectangles);
    }

    @Override
    public void render(SpriteBatch batch) {
        decoratedPlayer.render(batch);
    }
}

