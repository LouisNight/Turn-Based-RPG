package io.github.louisnight.turnbasedrpg.inventory;

import com.badlogic.gdx.graphics.Texture;

public class Shield extends Item {

    private float defenseBonus;

    public Shield(String name, String description, Texture texture, float defenseBonus) {
        super(name, description, texture);
        this.defenseBonus = defenseBonus;
    }

    public float getDefenseBonus() {
        return defenseBonus;
    }

    public void setDefenseBonus(float defenseBonus) {
        this.defenseBonus = defenseBonus;
    }

    @Override
    public void use() {
        System.out.println("Equipped shield: " + getName() + ". Defense increased by: " + defenseBonus);
    }
}
