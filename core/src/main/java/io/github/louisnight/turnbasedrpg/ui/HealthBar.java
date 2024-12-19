package io.github.louisnight.turnbasedrpg.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class HealthBar {
    private Image frameImage;
    private Image redBarImage;

    public HealthBar(Image frameImage, Image redBarImage) {
        this.frameImage = frameImage;
        this.redBarImage = redBarImage;
    }

    public void setSize(float width, float height) {
        frameImage.setSize(width, height);
        redBarImage.setSize(width, height);
    }

    public void removeFromStage() {
        frameImage.remove(); // Remove the frame image from the stage
        redBarImage.remove(); // Remove the red bar image from the stage
    }

    public Image getFrameImage() {
        return frameImage;
    }

    public Image getRedBarImage() {
        return redBarImage;
    }


    // Update the size of the red bar to reflect current health
    public void update(float currentHealth, float maxHealth) {
        // Calculate health percentage

        float healthPercentage = currentHealth / maxHealth;
        float newWidth = frameImage.getWidth() * healthPercentage;

        // Update red bar size and position
        redBarImage.setSize(newWidth, redBarImage.getHeight());
        redBarImage.setPosition(frameImage.getX() + (frameImage.getWidth() - newWidth), frameImage.getY());
    }

    // Set the position of the health bar
    public void setPosition(float x, float y) {
        frameImage.setPosition(x, y);
        redBarImage.setPosition(x, y);
    }

    // Add the health bar to the stage
    public void addToStage(Stage stage) {
        stage.addActor(frameImage);
        stage.addActor(redBarImage);
    }

    // Get the height of the health bar frame
    public float getHeight() {
        return frameImage.getHeight();
    }

    public float getWidth() {
        return frameImage.getWidth();
    }

}
