package io.github.louisnight.turnbasedrpg.inventory;

import com.badlogic.gdx.graphics.Texture;

public class Sword extends Item {
    private float damage;

    public Sword(String name, String description, Texture texture, float damage) {
        super(name, description, texture);
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    @Override
    public void use() {
        System.out.println("You swing the " + name + ", dealing " + damage + " damage!");
    }
}
