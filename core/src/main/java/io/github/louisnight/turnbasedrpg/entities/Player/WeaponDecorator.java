package io.github.louisnight.turnbasedrpg.entities.Player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class WeaponDecorator extends Player {

    private Player decoratedPlayer;
    private float additionalDamage;

    public WeaponDecorator(Player decoratedPlayer, float additionalDamage) {
        super(decoratedPlayer.getPosition().x, decoratedPlayer.getPosition().y, decoratedPlayer.getName());  // Call the parent constructor
        this.decoratedPlayer = decoratedPlayer;
        this.additionalDamage = additionalDamage;
    }

    @Override
    public float getDamage() {
        return decoratedPlayer.getDamage() + additionalDamage;
    }

    @Override
    public void setDamage(float damage) {
        decoratedPlayer.setDamage(damage);
    }

    @Override
    public void update(float delta, boolean moveUp, boolean moveDown, boolean moveLeft, boolean moveRight, ArrayList<Rectangle> collisionRectangles) {
        decoratedPlayer.update(delta, moveUp, moveDown, moveLeft, moveRight, collisionRectangles);
    }

    @Override
    public void render(SpriteBatch batch) {
        decoratedPlayer.render(batch);  // Delegate rendering to the decorated player
    }
}


