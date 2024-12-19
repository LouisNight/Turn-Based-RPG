package io.github.louisnight.turnbasedrpg.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

public class Inventory implements Screen {
    private Map<String, Integer> items;
    private Map<String, Texture> itemTextures;
    private Skin skin;
    private Stage stage;
    private Table inventoryTable;
    private boolean isVisible;
    private int selectedItemIndex = 1;

    public Inventory(Skin skin, Stage stage) {
        this.items = new HashMap<>();
        this.itemTextures = new HashMap<>();
        this.skin = skin;
        this.stage = stage;

        inventoryTable = new Table();
        float tableWidth = 200;
        float tableHeight = 300;

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float xPosition = 10;
        float yPosition = screenHeight - tableHeight - 10;

        inventoryTable.setSize(tableWidth, tableHeight);
        inventoryTable.setPosition(xPosition, yPosition);

        inventoryTable.top().left();


        inventoryTable.setVisible(false);

        stage.addActor(inventoryTable);

        loadItemTextures();

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
        inventoryTable.clear();

        Array<String> itemKeys = new Array<>(items.keySet().toArray(new String[0]));

        for (int i = 0; i < itemKeys.size; i++) {
            String item = itemKeys.get(i);
            int count = items.get(item);
            Texture texture = itemTextures.get(item);

            if (texture != null) {
                Image itemImage = new Image(texture);
                itemImage.setSize(50, 50);

                Label itemNameLabel = new Label(item + " x" + count, skin);

                Table rowTable = new Table();
                rowTable.add(itemImage).pad(10);       // Add item icon
                rowTable.add(itemNameLabel).pad(10);  // Add item name with count

                if (i == selectedItemIndex) {
                    rowTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("UI/highlight.png"))));
                }

                inventoryTable.add(rowTable).fillX().expandX().pad(5).row();
            }
        }
    }


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


    public void renderOverlay(SpriteBatch batch, OrthographicCamera camera) {
        if (isVisible) {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            batch.draw(
                new Texture(Gdx.files.internal("UI/inv_backdrop.png")),
                inventoryTable.getX(),
                inventoryTable.getY(),
                inventoryTable.getWidth(),
                inventoryTable.getHeight()
            );

            batch.end();
        }
    }


    public void handleInput() {
        if (!items.isEmpty()) {

            Array<String> itemKeys = new Array<>(items.keySet().toArray(new String[0]));

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.W)) {

                if (selectedItemIndex > 0) {
                    selectedItemIndex--;
                    updateInventoryUI();
                }
            } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S)) {

                if (selectedItemIndex < itemKeys.size - 1) {
                    selectedItemIndex++;
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

        stage.getViewport().update(width, height, true);

        float tableWidth = inventoryTable.getWidth();
        float tableHeight = inventoryTable.getHeight();

        inventoryTable.setPosition(10, height - tableHeight - 10);
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
