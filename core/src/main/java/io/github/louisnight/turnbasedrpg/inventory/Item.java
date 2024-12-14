package io.github.louisnight.turnbasedrpg.inventory;

import com.badlogic.gdx.graphics.Texture;

public abstract class Item {
    protected String name;
    protected String description;
    protected Texture texture;

    public Item(String name, String description, Texture texture) {
        this.name = name;
        this.description = description;
        this.texture = texture;
    }

    // Getters for name, description, and texture
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Texture getTexture() {
        return texture;
    }

    // Abstract method for using the item (to be implemented by specific items like Sword, Potion, etc.)
    public abstract void use();

}
