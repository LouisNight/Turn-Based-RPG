package io.github.louisnight.turnbasedrpg.entities;

public class EnemyFactory {

    public static Enemy createEnemy(String type, float x, float y) {
        switch (type.toLowerCase()) {
            case "orc":
                return new Orc(x, y);
            case "heavyorc":
                return new HeavyOrc(x,y);
            default:
                throw new IllegalArgumentException("Unknown enemy type: " + type);
        }
    }
}
