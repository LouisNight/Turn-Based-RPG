package io.github.louisnight.turnbasedrpg.inventory;

import com.badlogic.gdx.graphics.Texture;
import io.github.louisnight.turnbasedrpg.inventory.*;

public class ItemFactory {
    public static Item createItem(String itemName) {
        switch (itemName.toLowerCase()) {
            case "potion":
                return new Potion(
                    "Health Potion",
                    "Restores a moderate amount of health.",
                    new Texture("Items/health_potion.png"),
                    0.5f
                );
            case "sword":
                return new Sword(
                    "Wooden Sword",
                    "A shoddy sword made of wood.",
                    new Texture("Items/wood_sword.png"),
                    3.0f
                );
            case "shield":
                return new Shield(
                    "Wooden Shield",
                    "A sturdy wooden shield.",
                    new Texture("Items/wood_shield.png"),
                    5.0f
                );
            case "key":
                return new Key(
                        "Key",
                        "A key to unlock a door in a dungeon",
                        new Texture("Items/key.png")
                );
            default:
                throw new IllegalArgumentException("Unknown item: " + itemName);
        }
    }
}
