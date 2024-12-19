package io.github.louisnight.turnbasedrpg;

import com.badlogic.gdx.Game;
import io.github.louisnight.turnbasedrpg.views.*;

public class TestRPG extends Game {

    private LoadingScreen loadingScreen;
    private DungeonScreen gameScreen;
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


        loadingScreen = new LoadingScreen(this);
        menuScreen = new MenuScreen(this);  // Initialize MenuScreen
        gameScreen = new DungeonScreen(this);
        endScreen = new EndScreen(this);

        setScreen(menuScreen);  // Start at the menu screen
    }

    public MenuScreen getMenuScreen() {
        return menuScreen;
    }

    public DungeonScreen getGameScreen() {
        return gameScreen;
    }

    public void returnToOverworldWithWin() {
        gameScreen.returnToOverworldWithWin();
    }

    public void returnToOverworldWithLoss() {
        gameScreen.returnToOverworldWithLoss();
    }

    public AppPreferences getPreferences() {
        return preferences;
    }

    // Change screen based on user action
    public void changeScreen(int screen) {
        if (getScreen() != null) {
            getScreen().hide();
            if (getScreen() instanceof OptionsScreen) {
                ((OptionsScreen) getScreen()).dispose();
            }
        }

        switch (screen) {
            case MENU:
                if (menuScreen == null) menuScreen = new MenuScreen(this);
                setScreen(menuScreen);
                break;
            case PREFERENCES:
                setScreen(new OptionsScreen(this, getScreen()));
                break;
            case APPLICATION:
                if (gameScreen == null) gameScreen = new DungeonScreen(this);
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
        }

        super.dispose();
    }
}
