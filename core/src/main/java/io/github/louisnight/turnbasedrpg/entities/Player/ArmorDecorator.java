package io.github.louisnight.turnbasedrpg.entities.Player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class ArmorDecorator extends Player {

    private Player decoratedPlayer;
    private float additionalDefense;

    public ArmorDecorator(Player decoratedPlayer, float additionalDefense) {
        super(decoratedPlayer.getPosition().x, decoratedPlayer.getPosition().y, decoratedPlayer.getName());  // Call the parent constructor
        this.decoratedPlayer = decoratedPlayer;
        this.additionalDefense = additionalDefense;
    }

    @Override
    public float getDefense() {
        return decoratedPlayer.getDefense() + additionalDefense;
    }

    @Override
    public void setDefense(float defense) {
        decoratedPlayer.setDefense(defense);
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
