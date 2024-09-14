package io.github.louisnight.turnbasedrpg.views;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.louisnight.turnbasedrpg.TestRPG;
import com.badlogic.gdx.Screen;

public class PreferencesScreen implements Screen {
    // storing orchestrator
    private final TestRPG parent;

    // constructor with core/main argument
    public PreferencesScreen(TestRPG testRPG) {
        parent = testRPG;

        // setup user input using stage
        Gdx.input.setInputProcessor(stage);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    private final Stage stage = new Stage(new ScreenViewport());

    private TextButton fullscreen;
    private TextButton back;

    private int selectedIndex = 0;
    private boolean isFullscreen = false;

    @Override
    public void show() {
        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        stage.addActor(table);

        // adds buttons to Main Menu
        Skin skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));

        fullscreen = new TextButton("Fullscreen", skin);
        back = new TextButton("Back", skin);

        // input from the buttons
        fullscreen.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isFullscreen) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                    isFullscreen = true;
                } else {
                    Gdx.graphics.setWindowedMode(1080, 720);
                    isFullscreen = false;
                }
            }
        });
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen(parent));
            }
        });


        // adds table to stage for Main Menu
        table.add(fullscreen).fillX().uniformX();
        table.row().pad(10, 0, 10, 0);
        table.add(back).fillX().uniformX();

        Gdx.input.setInputProcessor(stage);
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keyCode) {
                switch (keyCode) {
                    case Input.Keys.UP:
                        navigateUp();
                        return true;
                    case Input.Keys.DOWN:
                        navigateDown();
                        return true;
                    case Input.Keys.ENTER:
                        activateButton();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void navigateUp() {
        selectedIndex = (selectedIndex - 1 + 2) % 2;
        updateSelection();
    }

    private void navigateDown() {
        selectedIndex = (selectedIndex + 1) % 2;
        updateSelection();
    }

    private void activateButton() {
        switch (selectedIndex) {
            case 0:
                fullscreen.getListeners().forEach(listener -> {
                    if (listener instanceof ClickListener) {
                        ((ClickListener) listener).clicked(null, 0, 0);
                    }
                });
                break;
            case 1:
                back.getListeners().forEach(listener -> {
                    if (listener instanceof ClickListener) {
                        ((ClickListener) listener).clicked(null, 0, 0);
                    }
                });
                break;
        }
    }

    private void updateSelection() {
        fullscreen.setColor(selectedIndex == 0 ? 1 : 0.5f, selectedIndex == 0 ? 1 : 0.5f, selectedIndex == 0 ? 1 : 0.5f, 1);
        back.setColor(selectedIndex == 1 ? 1 : 0.5f, selectedIndex == 1 ? 1 : 0.5f, selectedIndex == 1 ? 1 : 0.5f, 1);
    }

    @Override
    public void render(float v) {
        // clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();


    }

    @Override
    public void resize(int i, int i1) {

        stage.getViewport().update(i, i1, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
