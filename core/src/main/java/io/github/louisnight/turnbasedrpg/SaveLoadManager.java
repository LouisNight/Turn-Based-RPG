package io.github.louisnight.turnbasedrpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import io.github.louisnight.turnbasedrpg.entities.Player.Player;


public class SaveLoadManager {
    private static final String PREFERENCES_NAME = "TestRPGSAVE";
    private static SaveLoadManager instance;

    private Preferences preferences;

    private SaveLoadManager() {
        preferences = Gdx.app.getPreferences(PREFERENCES_NAME);
    }

    public static SaveLoadManager getInstance() {
        if (instance == null) {
            instance = new SaveLoadManager();
        }
        return instance;
    }

    public void saveGame(Player player) {
        preferences.putString("playerName", player.getName());
        preferences.putFloat("playerX", player.getPosition().x);
        preferences.putFloat("playerY", player.getPosition().y);
        preferences.putFloat("playerHealth", player.getHealth());
        preferences.putFloat("playerMaxHealth", player.getMaxHealth());
        preferences.flush(); // Ensure changes are saved
        System.out.println("Game saved successfully.");
    }

    public void loadGame(Player player) {
        if (preferences.contains("playerName")) {
            player.setPosition(preferences.getFloat("playerX"), preferences.getFloat("playerY"));
            player.setHealth(preferences.getFloat("playerHealth"));
            player.setMaxHealth(preferences.getFloat("playerMaxHealth"));
            System.out.println("Game loaded successfully for player: " + player.getName());
        } else {
            System.out.println("No saved game found.");
        }
    }
}
