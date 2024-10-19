package io.github.louisnight.turnbasedrpg.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Item> items;
    private Table inventoryTable;
    private Table characterTable;
    private DragAndDrop dragAndDrop;
    private Stage inventoryStage;

    private Texture weaponSlotTexture;
    private Texture shieldSlotTexture;
    private Texture accessorySlotTexture;

    public Inventory(Skin skin, Stage inventoryStage) {
        items = new ArrayList<>();
        this.inventoryStage = inventoryStage;
        inventoryTable = new Table();
        characterTable = new Table();
        dragAndDrop = new DragAndDrop();

        weaponSlotTexture = new Texture("../assets/UI/weapon_slot.png");
        shieldSlotTexture = new Texture("../assets/UI/shield_slot.png");
        accessorySlotTexture = new Texture("../assets/UI/accessory_slot.png");

        inventoryTable.setFillParent(true);
        inventoryTable.center();

        float scaleFactor = Math.min(Gdx.graphics.getWidth() / 1920f, Gdx.graphics.getHeight() / 1080f) * 1.5f;

        for (int i = 0; i < 9; i++) {
            Stack slotStack = new Stack();
            Image slotImage = new Image(new Texture("../assets/UI/basic_slot.png"));
            slotImage.setSize(96 * scaleFactor, 96 * scaleFactor);

            slotStack.add(slotImage);

            if (i == 0) {
                Image swordImage = new Image(new Texture("../assets/Items/wood_sword.png"));
                swordImage.setSize(96 * scaleFactor, 96 * scaleFactor);
                slotStack.add(swordImage);

                dragAndDrop.addSource(new DragAndDrop.Source(swordImage) {
                    @Override
                    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                        DragAndDrop.Payload payload = new DragAndDrop.Payload();
                        payload.setObject(swordImage);

                        Image dragImage = new Image(swordImage.getDrawable());
                        dragImage.setSize(swordImage.getWidth(), swordImage.getHeight());
                        payload.setDragActor(dragImage);
                        return payload;
                    }
                });
            }

            inventoryTable.add(slotStack).size(96 * scaleFactor, 96 * scaleFactor).pad(15 * scaleFactor);

            if ((i + 1) % 3 == 0) {
                inventoryTable.row();
            }
        }

        characterTable.setFillParent(true);
        characterTable.top().right();

        Image weaponSlotImage = new Image(weaponSlotTexture);
        Image shieldSlotImage = new Image(shieldSlotTexture);
        Image accessorySlotImage = new Image(accessorySlotTexture);

        float equipmentSlotSize = Math.max(120 * scaleFactor, 80); // Set a minimum size of 80 for equipment slots

        weaponSlotImage.setSize(equipmentSlotSize, equipmentSlotSize);
        shieldSlotImage.setSize(equipmentSlotSize, equipmentSlotSize);
        accessorySlotImage.setSize(equipmentSlotSize, equipmentSlotSize);

        characterTable.add(weaponSlotImage).pad(5);
        dragAndDrop.addTarget(createEquipmentSlotTarget(weaponSlotImage));

        characterTable.add(shieldSlotImage).pad(5);
        dragAndDrop.addTarget(createEquipmentSlotTarget(shieldSlotImage));

        characterTable.add(accessorySlotImage).pad(5);
        dragAndDrop.addTarget(createEquipmentSlotTarget(accessorySlotImage));

        inventoryStage.addActor(characterTable);
        inventoryStage.addActor(inventoryTable);
    }

    private DragAndDrop.Target createEquipmentSlotTarget(final Image slotImage) {
        return new DragAndDrop.Target(slotImage) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                return true;
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                Image draggedItem = (Image) payload.getObject();
                slotImage.setDrawable(draggedItem.getDrawable());
            }
        };
    }

    public void render(float delta, SpriteBatch batch) {
        // Update the stage's actions
        inventoryStage.act(delta);

        // Set the batch's projection matrix for correct rendering
        batch.setProjectionMatrix(inventoryStage.getCamera().combined);

        batch.begin();
        // Draw a semi-transparent overlay for the inventory background
        batch.setColor(0, 0, 0, 0.7f); // Set color to semi-transparent black
        batch.draw(new Texture(Gdx.files.internal("UI/inv_backdrop.png")), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(1, 1, 1, 1); // Reset color to white for further drawing
        batch.end();

        // Draw the stage after the batch end
        inventoryStage.draw();
    }



    public List<Item> getItems() {
        return items;
    }

    public Stage getStage() {
        return inventoryStage;
    }

    public void resize(int width, int height) {
        inventoryStage.getViewport().update(width, height, true);
        float scaleFactor = Math.min(width / 1920f, height / 1080f) * 1.5f; // Adjust scale factor
        float slotSize = Math.max(96 * scaleFactor, 64); // Minimum size of 64 for inventory slots

        for (Cell<?> cell : inventoryTable.getCells()) {
            cell.size(slotSize, slotSize);
        }

        float equipmentSlotSize = Math.max(120 * scaleFactor, 80); // Minimum size of 80 for equipment slots
        for (Cell<?> cell : characterTable.getCells()) {
            cell.size(equipmentSlotSize, equipmentSlotSize);
        }

        inventoryTable.pad(10 * scaleFactor);
    }

    public void dispose() {
        inventoryStage.dispose();
        weaponSlotTexture.dispose();
        shieldSlotTexture.dispose();
        accessorySlotTexture.dispose();
    }
}
