package io.github.louisnight.turnbasedrpg.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

public class Inventory implements Screen {
    private Map<String, Integer> items; // Map to store items and their counts
    private Map<String, Texture> itemTextures; // Map item names to textures
    private Skin skin;
    private Stage stage;
    private Table inventoryTable; // To hold the inventory UI layout
    private boolean isVisible; // Flag to control visibility of the inventory
    private int selectedItemIndex = 1; // Index to track the selected item

    public Inventory(Skin skin, Stage stage) {
        this.items = new HashMap<>();
        this.itemTextures = new HashMap<>();
        this.skin = skin;
        this.stage = stage;

        // Initialize inventory layout
        inventoryTable = new Table();
        float tableWidth = 200;  // Width of the inventory table
        float tableHeight = 300; // Height of the inventory table

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float xPosition = 10; // 10 pixels from the left edge
        float yPosition = screenHeight - tableHeight - 10; // 10 pixels from the top edge

        inventoryTable.setSize(tableWidth, tableHeight);
        inventoryTable.setPosition(xPosition, yPosition);

        inventoryTable.top().left();


        inventoryTable.setVisible(false); // Initially, the inventory is hidden

        // Add the inventory table to the stage
        stage.addActor(inventoryTable);

        // Load textures for known items
        loadItemTextures();

        // Initialize selected item index
        selectedItemIndex = 0;
    }

    private void loadItemTextures() {
        // Load textures for your items; make sure these paths are correct
        itemTextures.put("potion", new Texture(Gdx.files.internal("Items/health_potion.png")));
        itemTextures.put("shield", new Texture(Gdx.files.internal("Items/wood_shield.png")));
        itemTextures.put("sword", new Texture(Gdx.files.internal("Items/wood_sword.png")));
        itemTextures.put("key", new Texture(Gdx.files.internal("Items/key.png")));
        // Add more items as needed
    }

    public void addItem(String item) {
        items.put(item, items.getOrDefault(item, 0) + 1);
        updateInventoryUI();
        System.out.println("Added item to inventory: " + item);
    }

    public void addItems(Array<String> newItems) {
        for (String item : newItems) {
            items.put(item, items.getOrDefault(item, 0) + 1);
        }
        updateInventoryUI();
        System.out.println("Added multiple items to inventory.");
    }

    private void updateInventoryUI() {
        // Clear existing UI elements
        inventoryTable.clear();

        // Convert the keys to an indexed array for consistency
        Array<String> itemKeys = new Array<>(items.keySet().toArray(new String[0]));

        // Render each item in the inventory
        for (int i = 0; i < itemKeys.size; i++) {
            String item = itemKeys.get(i);
            int count = items.get(item);
            Texture texture = itemTextures.get(item);

            if (texture != null) {
                // Create UI elements for the item
                Image itemImage = new Image(texture);
                itemImage.setSize(50, 50);

                // Display item name and count (e.g., "potion x2")
                Label itemNameLabel = new Label(item + " x" + count, skin);

                // Add elements to a new row
                Table rowTable = new Table();
                rowTable.add(itemImage).pad(10);       // Add item icon
                rowTable.add(itemNameLabel).pad(10);  // Add item name with count

                // Highlight selected item
                if (i == selectedItemIndex) {
                    rowTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("UI/highlight.png"))));
                }

                // Add the row to the main inventory table
                inventoryTable.add(rowTable).fillX().expandX().pad(5).row();
            }
        }
    }


    // Toggle the visibility of the inventory UI
    public void toggleVisibility() {
        isVisible = !isVisible;
        inventoryTable.setVisible(isVisible);
    }

    public Stage getStage() {
        return stage;
    }

    public boolean isVisible() {
        return isVisible;
    }

    // Method to render the inventory background overlay (if needed)
// Method to render the inventory background overlay (if needed)
    public void renderOverlay(SpriteBatch batch, OrthographicCamera camera) {
        if (isVisible) {
            batch.setProjectionMatrix(camera.combined); // Ensure batch uses the camera's coordinate system
            batch.begin();

            // Render the backdrop in the correct position with the correct size
            batch.draw(
                new Texture(Gdx.files.internal("UI/inv_backdrop.png")),
                inventoryTable.getX(), // Use the inventory table's X position
                inventoryTable.getY(), // Use the inventory table's Y position
                inventoryTable.getWidth(), // Use the inventory table's width
                inventoryTable.getHeight() // Use the inventory table's height
            );

            batch.end();
        }
    }


    public void handleInput() {
        if (!items.isEmpty()) {
            // Convert the keys of the map (Set<String>) to an Array for indexed access
            Array<String> itemKeys = new Array<>(items.keySet().toArray(new String[0]));

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.W)) {
                // Scroll up but stop at the first item
                if (selectedItemIndex > 0) {
                    selectedItemIndex--; // Move selection up
                    updateInventoryUI();
                }
            } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S)) {
                // Scroll down but stop at the last item
                if (selectedItemIndex < itemKeys.size - 1) {
                    selectedItemIndex++; // Move selection down
                    updateInventoryUI();
                }
            }
        }
    }






    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Update the viewport of the stage to handle resizing
        stage.getViewport().update(width, height, true);

        // Now set the position of the inventory table based on the new window size
        float tableWidth = inventoryTable.getWidth();
        float tableHeight = inventoryTable.getHeight();

        // Recalculate position to always be in the top-left corner
        inventoryTable.setPosition(10, height - tableHeight - 10); // 10px from the top-left corner
    }


    public void pause() {}

    public void show() {
        Gdx.input.setInputProcessor(stage);
        inventoryTable.setVisible(true);
    }

    public void hide() {
        Gdx.input.setInputProcessor(null);
        inventoryTable.setVisible(false);
    }

    public void resume() {}

    @Override
    public void dispose() {
        for (Texture texture : itemTextures.values()) {
            texture.dispose();
        }
    }
}
