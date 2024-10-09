package io.github.louisnight.turnbasedrpg.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.louisnight.turnbasedrpg.TestRPG;

public class OptionsScreen implements Screen {

    private final TestRPG parent;
    // our new fields
    private Label titleLabel;
    private Label volumeMusicLabel;
    private Label volumeSoundLabel;
    private Label musicOnOffLabel;
    private Label soundOnOffLabel;
    private final Stage stage = new Stage(new ScreenViewport());

    // constructor with core/main argument
    public OptionsScreen(TestRPG testRPG) {
        parent = testRPG;

        stage.clear();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }


    @Override
    public void show() {
        stage.clear();
        Skin skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));

        //volume
        final Slider volumeMusicSlider = new Slider(0f, 1f, 0.1f, false, skin);
        volumeMusicSlider.setValue(parent.getPreferences().getMusicVolume());
        volumeMusicSlider.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                parent.getPreferences().setMusicVolume(volumeMusicSlider.getValue());
                return false;
            }
        });

        //music
        final CheckBox musicCheckbox = new CheckBox(null, skin);
        musicCheckbox.setChecked(parent.getPreferences().isMusicEnabled());
        musicCheckbox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                boolean enabled = musicCheckbox.isChecked();
                parent.getPreferences().setMusicEnabled(enabled);
                return false;
            }
        });
        //fullscreen
        final TextButton fullscreenButton = new TextButton("Toggle Fullscreen", skin);
        fullscreenButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleFullscreen();
            }
        });

        // return to main screen button
        final TextButton backButton = new TextButton("Back", skin); // the extra argument here "small" is used to set the button to the smaller version instead of the big default version
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                parent.changeScreen(TestRPG.MENU);
            }
        });


        Table table = new Table();
        table.setFillParent(true);
        // table.setDebug(true);
        stage.addActor(table);

        // adds buttons to Preferences Menu

        Slider soundMusicSlider = new Slider(0f, 100f, 0.5f, false, skin);
        CheckBox soundEffectsCheckbox = new CheckBox(null, skin);

        titleLabel = new Label("Options", skin);
        volumeMusicLabel = new Label("Music", skin);
        volumeSoundLabel = new Label("Sound Effects", skin);
        musicOnOffLabel = new Label("Music On/Off", skin);
        soundOnOffLabel = new Label("Sound On/Off", skin);

        table.add(titleLabel).colspan(2);
        table.row().pad(10, 0, 0, 10);
        table.add(volumeMusicLabel).left();
        table.add(volumeMusicSlider);
        table.row().pad(10, 0, 0, 10);
        table.add(musicOnOffLabel).left();
        table.add(musicCheckbox);
        table.row().pad(10, 0, 0, 10);
        table.add(volumeSoundLabel).left();
        table.add(soundMusicSlider);
        table.row().pad(10, 0, 0, 10);
        table.add(soundOnOffLabel).left();
        table.add(soundEffectsCheckbox);
        table.row().pad(10, 0, 0, 10);
        table.add(fullscreenButton).colspan(2);
        table.row().pad(10, 0, 0, 10);
        table.add(backButton).colspan(2);

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    private void toggleFullscreen() {
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(1000, 800);
        } else {
            Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
            Gdx.graphics.setFullscreenMode(displayMode);
        }
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
        stage.dispose();
    }
}
