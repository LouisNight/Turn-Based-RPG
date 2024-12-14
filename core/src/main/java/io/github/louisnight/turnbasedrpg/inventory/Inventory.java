package io.github.louisnight.turnbasedrpg.inventory;

import com.badlogic.gdx.Gdx;
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

public class Inventory implements Disposable {
    private Array<String> items; // Items represented as strings (item IDs or names)
    private Map<String, Texture> itemTextures; // Map item names to textures
    private Skin skin;
    private Stage stage;
    private Table inventoryTable; // To hold the inventory UI layout
    private boolean isVisible; // Flag to control visibility of the inventory
    private int selectedItemIndex; // Index to track the selected item

    public Inventory(Skin skin, Stage stage) {
        this.items = new Array<>();
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
        float yPosition = screenHeight - tableHeight - 200; // 10 pixels from the top edge

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
        // Add more items as needed
    }

    public void addItem(String item) {
        items.add(item);
        updateInventoryUI();
        System.out.println("Added item to inventory: " + item);
    }

    public void addItems(Array<String> newItems) {
        items.addAll(newItems);
        updateInventoryUI();
        System.out.println("Added multiple items to inventory.");
    }

    private void updateInventoryUI() {
        // Clear existing UI elements
        inventoryTable.clear();

        // Render each item in the inventory
        for (int i = 0; i < items.size; i++) {
            String item = items.get(i);
            Texture texture = itemTextures.get(item);
            if (texture != null) {
                // Create UI elements for the item
                Image itemImage = new Image(texture);
                itemImage.setSize(50, 50);
                Label itemNameLabel = new Label(item, skin);

                // Add elements to a new row
                Table rowTable = new Table();
                rowTable.add(itemImage).pad(10);   // Add item icon
                rowTable.add(itemNameLabel).pad(10); // Add item name

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
            batch.draw(
                new Texture(Gdx.files.internal("UI/inv_backdrop.png")),
                camera.position.x - camera.viewportWidth / 2, // Center the overlay horizontally
                camera.position.y - camera.viewportHeight / 2, // Center the overlay vertically
                camera.viewportWidth, // Match the camera's viewport width
                camera.viewportHeight // Match the camera's viewport height
            );
            batch.end();
        }
    }

    // Handle cycling through items
    public void handleInput() {
        if (items.size > 0) {
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.W)) {
                selectedItemIndex = (selectedItemIndex - 1 + items.size) % items.size;
                updateInventoryUI(); // Update UI after selection change
            } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S)) {
                selectedItemIndex = (selectedItemIndex + 1) % items.size;
                updateInventoryUI(); // Update UI after selection change
            }
        }
    }


    @Override
    public void dispose() {
        for (Texture texture : itemTextures.values()) {
            texture.dispose();
        }
    }
}
