package io.github.louisnight.turnbasedrpg.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

public class Inventory implements Screen {
    private final Stage stage;
    private final Table inventoryTable;
    private final Map<String, Texture> itemTextures;
    private final Array<String> items;
    private final Skin skin;

    public Inventory(Skin skin) {
        this.skin = skin;
        this.stage = new Stage();
        this.items = new Array<>();
        this.itemTextures = new HashMap<>();

        // Set up inventory UI
        inventoryTable = new Table();
        inventoryTable.setFillParent(true);
        inventoryTable.setVisible(false);
        inventoryTable.debug(); // Enable debug lines for visualization
        stage.addActor(inventoryTable);

        loadItemTextures();
        updateInventoryUI();
    }

    private void loadItemTextures() {
        itemTextures.put("potion", new Texture(Gdx.files.internal("Items/health_potion.png")));
        itemTextures.put("shield", new Texture(Gdx.files.internal("Items/wood_shield.png")));
    }

    private void updateInventoryUI() {
        inventoryTable.clear(); // Clear previous items
        inventoryTable.top();   // Align table to start from the top

        // Add title row
        Label titleLabel = new Label("Inventory", skin);
        titleLabel.setFontScale(1.5f); // Optionally increase the font size
        inventoryTable.add(titleLabel).colspan(2).padBottom(20).center().row();

        // Add items
        for (String item : items) {
            Texture texture = itemTextures.get(item);
            if (texture != null) {
                Image itemImage = new Image(texture);
                itemImage.setSize(50, 50);
                Label itemNameLabel = new Label(item, skin);

                Table row = new Table();
                row.add(itemImage).pad(10);
                row.add(itemNameLabel).pad(10);

                inventoryTable.add(row).fillX().expandX().pad(5).row();
            }
        }

        inventoryTable.layout(); // Refresh layout
    }


    public void addItem(String item) {
        items.add(item);
        updateInventoryUI();
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        inventoryTable.setVisible(true);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        inventoryTable.setVisible(false);
    }

    @Override
    public void dispose() {
        for (Texture texture : itemTextures.values()) {
            texture.dispose();
        }
        stage.dispose();
    }
}
