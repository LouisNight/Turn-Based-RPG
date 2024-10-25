package io.github.louisnight.turnbasedrpg.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScreenTransition {

    private float fadeAlpha;       // Alpha value for the fade effect
    private boolean isFadingOut;   // Is the screen currently fading out?
    private boolean isFadingIn;    // Is the screen currently fading in?
    private float fadeSpeed;       // Speed of the fade effect
    private Texture fadeTexture;   // Texture used for the fade overlay

    public ScreenTransition(float fadeSpeed) {
        this.fadeAlpha = 0;
        this.isFadingOut = false;
        this.isFadingIn = false;
        this.fadeSpeed = fadeSpeed;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        fadeTexture = new Texture(pixmap);

        pixmap.dispose();
    }

    // Start the fade-out transition
    public void startFadeOut() {
        isFadingOut = true;
        fadeAlpha = 0;
    }

    // Start the fade-in transition
    public void startFadeIn() {
        isFadingIn = true;
        fadeAlpha = 1;
    }

    // Update the fade effect, delta is the time passed since the last frame
    public void update(float delta) {
        if (isFadingOut) {
            fadeAlpha += fadeSpeed * delta;
            if (fadeAlpha >= 1) {
                fadeAlpha = 1;
                isFadingOut = false;
                // The screen is fully faded out, you can now switch scenes
            }
        } else if (isFadingIn) {
            fadeAlpha -= fadeSpeed * delta;
            if (fadeAlpha <= 0) {
                fadeAlpha = 0;
                isFadingIn = false;
                // The screen is fully faded in
            }
        }
    }

    // Render the fade effect, drawing a black rectangle over the screen
    public void render(SpriteBatch batch) {
        if (isFadingOut || isFadingIn) {
            Gdx.gl.glEnable(GL20.GL_BLEND);  // Enable transparency
            batch.setColor(0, 0, 0, fadeAlpha);  // Set the black color with current alpha
            batch.draw(fadeTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(1, 1, 1, 1);  // Reset color to default
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    // Dispose of resources
    public void dispose() {
        if (fadeTexture != null) {
            fadeTexture.dispose();
        }
    }

    // Getters to check if fading in or out
    public boolean isFadingOut() {
        return isFadingOut;
    }

    public boolean isFadingIn() {
        return isFadingIn;
    }

    public boolean isTransitioning() {
        return isFadingOut || isFadingIn;
    }
}
