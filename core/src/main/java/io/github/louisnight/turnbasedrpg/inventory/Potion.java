package io.github.louisnight.turnbasedrpg.inventory;

import com.badlogic.gdx.graphics.Texture;

public class Potion extends Item {
    private float healingAmount;

    public Potion(String name, String description, Texture texture, float healingAmount) {
        super(name, description, texture);
        this.healingAmount = healingAmount;
    }

    public float getHealingAmount() {
        return healingAmount;
    }

    @Override
    public void use() {
        System.out.println("You drink the " + name + " and heal for " + healingAmount + " HP!");
    }
}
