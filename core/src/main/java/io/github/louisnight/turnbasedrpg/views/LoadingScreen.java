package io.github.louisnight.turnbasedrpg.views;

import io.github.louisnight.turnbasedrpg.TestRPG;
import com.badlogic.gdx.Screen;

public class LoadingScreen implements Screen {
    // storing orchestrator
    private final TestRPG parent;

    // constructor
    public LoadingScreen(TestRPG testRPG) {
        parent = testRPG;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        parent.changeScreen(TestRPG.MENU);
    }

    @Override
    public void resize(int i, int i1) {

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
