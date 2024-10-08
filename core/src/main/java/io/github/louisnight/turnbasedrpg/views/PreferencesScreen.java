package io.github.louisnight.turnbasedrpg.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.louisnight.turnbasedrpg.TestRPG;

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

    private final Stage stage = new Stage(new ScreenViewport());// storing orchestrator

    private CharSequence fullscreen;

    @Override
    public void show() {
        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        stage.addActor(table);

        // adds buttons to Main Menu
        Skin skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));

        TextButton fullscreen = new TextButton("Fullscreen", skin);

        table.add(fullscreen).fillX().uniformX();
        table.row().pad(10, 0, 10, 0);
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
