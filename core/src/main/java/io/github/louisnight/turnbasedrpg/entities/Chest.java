package io.github.louisnight.turnbasedrpg.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Chest {
    private Vector2 position;
    private Array<String> items;
    private boolean opened;

    private static Texture chestClosedTexture; // Texture for closed chest
    private static Texture chestOpenedTexture; // Texture for opened chest

    public Chest() {
        // Required no-arg constructor for JSON deserialization
        opened = false; // Initialize as unopened
    }

    public Chest(float x, float y, Array<String> items) {
        this.position = new Vector2(x, y);
        this.items = items;
        this.opened = false;
    }

    static {
        // Load both chest textures
        chestClosedTexture = new Texture("MapAssets/chest_closed.png");
        chestOpenedTexture = new Texture("MapAssets/chest_opened.png");
    }

    public Vector2 getPosition() {
        return position;
    }

    public Array<String> getItems() {
        return items;
    }

    public boolean isOpened() {
        return opened;
    }

    public void open() {
        this.opened = true;
    }

    public void render(SpriteBatch batch) {
        // Draw the appropriate chest texture based on the 'opened' state
        if (opened) {
            batch.draw(chestOpenedTexture, position.x, position.y);
        } else {
            batch.draw(chestClosedTexture, position.x, position.y);
        }
    }

    public static void dispose() {
        chestClosedTexture.dispose();
        chestOpenedTexture.dispose();
    }
}
