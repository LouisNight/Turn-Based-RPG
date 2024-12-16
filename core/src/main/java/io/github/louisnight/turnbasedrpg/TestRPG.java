package io.github.louisnight.turnbasedrpg;

import com.badlogic.gdx.Game;
import io.github.louisnight.turnbasedrpg.views.*;

public class TestRPG extends Game {

    private LoadingScreen loadingScreen;
    private GameScreen gameScreen;
    private EndScreen endScreen;
    private MenuScreen menuScreen;
    private AppPreferences preferences;

    public final static int MENU = 0;
    public final static int PREFERENCES = 1;
    public final static int APPLICATION = 2;
    public final static int ENDGAME = 3;

    @Override
    public void create() {
        preferences = new AppPreferences();

        // Initialize screens (but don't display yet)
        loadingScreen = new LoadingScreen(this);
        menuScreen = new MenuScreen(this);  // Initialize MenuScreen
        gameScreen = new GameScreen(this);
        endScreen = new EndScreen(this);

        // Set the initial screen (menu screen)
        setScreen(menuScreen);  // Start at the menu screen
    }

    public MenuScreen getMenuScreen() {
        return menuScreen;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    // Return to overworld after winning combat
    public void returnToOverworldWithWin() {
        gameScreen.returnToOverworldWithWin();
    }

    // Return to overworld after losing combat
    public void returnToOverworldWithLoss() {
        gameScreen.returnToOverworldWithLoss();
    }

    public AppPreferences getPreferences() {
        return preferences;
    }

    // Change screen based on user action
    public void changeScreen(int screen) {
        if (getScreen() != null) getScreen().hide(); // Hide the current screen

        switch (screen) {
            case MENU:
                if (menuScreen == null) menuScreen = new MenuScreen(this);
                setScreen(menuScreen);
                break;
            case PREFERENCES:
                setScreen(new OptionsScreen(this, getScreen())); // Pass the current screen as the return screen
                break;
            case APPLICATION:
                if (gameScreen == null) gameScreen = new GameScreen(this);
                setScreen(gameScreen);
                break;
            case ENDGAME:
                if (endScreen == null) endScreen = new EndScreen(this);
                setScreen(endScreen);
                break;
        }
    }
    @Override
    public void dispose() {
        if (loadingScreen != null) loadingScreen.dispose();
        if (menuScreen != null) menuScreen.dispose();
        if (gameScreen != null) gameScreen.dispose();
        if (endScreen != null) endScreen.dispose();

        if (preferences != null) {
            // If AppPreferences uses external resources, clean them up
        }

        super.dispose();
    }
}
