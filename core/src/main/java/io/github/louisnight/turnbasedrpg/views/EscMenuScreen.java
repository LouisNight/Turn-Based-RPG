package io.github.louisnight.turnbasedrpg.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.louisnight.turnbasedrpg.SaveLoadManager;
import io.github.louisnight.turnbasedrpg.TestRPG;
import io.github.louisnight.turnbasedrpg.entities.Player.Player;

public class EscMenuScreen implements Screen {

    private final TestRPG parent;
    private final Stage stage;
    private final Player player;
    private boolean visible = false;
    private boolean optionsMenuOpen = false;

    public EscMenuScreen(TestRPG parent, Player player) {
        this.parent = parent;
        this.player = player;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);


        Skin skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));

        Table table = new Table();
        table.setFillParent(true);
        table.setVisible(true);
        stage.addActor(table);

        TextButton resumeButton = new TextButton("Resume Game", skin);
        TextButton saveButton = new TextButton("Save Game", skin);
        TextButton loadButton = new TextButton("Load Game", skin);
        TextButton optionsButton = new TextButton("Options", skin);
        TextButton quitButton = new TextButton("Quit to Main Menu", skin);

        table.add(resumeButton).fillX().uniformX();
        table.row().pad(10, 0, 10, 0);
        table.add(saveButton).fillX().uniformX();
        table.row();
        table.add(loadButton).fillX().uniformX();
        table.row().pad(10, 0, 10, 0);
        table.add(optionsButton).fillX().uniformX();
        table.row();
        table.add(quitButton).fillX().uniformX();

        table.layout();

        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.getGameScreen().toggleEscMenu(); // Close the ESC Menu
                parent.setScreen(parent.getGameScreen()); // Return to the game screen
            }
        });

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SaveLoadManager.getInstance().saveGame(player); // Save game state
            }
        });

        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SaveLoadManager.getInstance().loadGame(player); // Load game state
            }
        });

        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.setScreen(new OptionsScreen(parent, EscMenuScreen.this)); // Go to options screen, returning to ESC menu on back
            }
        });

        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.setScreen(parent.getMenuScreen()); // Go back to the main menu
            }
        });
    }

    public boolean isOptionsMenuOpen() {
        return optionsMenuOpen;
    }

    public void closeOptionsMenu() {
        optionsMenuOpen = false; // Reset the state
        Gdx.input.setInputProcessor(stage); // Restore ESC Menu input processor
        System.out.println("EscMenuScreen: Options menu closed");

        if (parent.getScreen() instanceof OptionsScreen) {
            ((OptionsScreen) parent.getScreen()).dispose(); // Explicitly dispose of options screen resources
        }
    }


    public Stage getStage() {
        return stage;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        System.out.println("ESC Menu Visibility Set to: " + visible); // Debug log
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {

        if (optionsMenuOpen) {
            System.out.println("EscMenuScreen: Skipping render (options menu open)");
            return;
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
