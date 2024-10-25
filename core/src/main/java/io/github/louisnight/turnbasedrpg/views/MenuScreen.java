package io.github.louisnight.turnbasedrpg.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.louisnight.turnbasedrpg.TestRPG;

public class MenuScreen implements Screen {
    private final TestRPG parent;
    private final Stage stage;

    // Constructor with the main game argument
    public MenuScreen(TestRPG testRPG) {
        parent = testRPG;
        stage = new Stage(new ScreenViewport());  // Initialize stage here
    }

    @Override
    public void show() {
        // Set input processor to this stage (for buttons to work)
        Gdx.input.setInputProcessor(stage);

        // Setup table layout for buttons
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Load skin for buttons
        Skin skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));

        // Create buttons
        TextButton newGame = new TextButton("New Game", skin);
        TextButton preferences = new TextButton("Options", skin);
        TextButton exit = new TextButton("Exit", skin);

        // Add buttons to table with padding
        table.add(newGame).fillX().uniformX();
        table.row().pad(10, 0, 10, 0);
        table.add(preferences).fillX().uniformX();
        table.row();
        table.add(exit).fillX().uniformX();

        // Listener for "Exit" button
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        // Listener for "New Game" button
        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(TestRPG.APPLICATION);  // Start the game
            }
        });

        // Listener for "Preferences" button
        preferences.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(TestRPG.PREFERENCES);  // Open options screen
            }
        });
    }

    @Override
    public void render(float delta) {
        // Clear the screen with a black color
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Tell stage to perform actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Adjust stage viewport when the window is resized
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
        // Dispose of the stage to free resources
        stage.dispose();
    }
}
