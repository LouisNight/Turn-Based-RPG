package io.github.louisnight.turnbasedrpg.inventory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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

        // Initialize the inventory grid
        for (int i = 0; i < 9; i++) {
            Stack slotStack = new Stack();
            Image slotImage = new Image(new Texture("../assets/UI/basic_slot.png"));

            slotStack.add(slotImage);

            // Adding an item in the first slot
            if (i == 0) {
                Image swordImage = new Image(new Texture("../assets/Items/wood_sword.png"));
                slotStack.add(swordImage);

                // Set up drag source for this item
                dragAndDrop.addSource(new DragAndDrop.Source(swordImage) {
                    @Override
                    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                        DragAndDrop.Payload payload = new DragAndDrop.Payload();
                        payload.setObject(swordImage);  // Pass the item being dragged

                        Image dragImage = new Image(swordImage.getDrawable());
                        payload.setDragActor(dragImage);  // Show the item during the drag
                        return payload;
                    }
                });
            }
            inventoryTable.add(slotStack).size(64, 64).pad(10);

            if ((i + 1) % 3 == 0) {
                inventoryTable.row();
            }
        }

        characterTable.setFillParent(true);
        characterTable.top().left();

        Image weaponSlotImage = new Image(weaponSlotTexture);
        Image shieldSlotImage = new Image(shieldSlotTexture);
        Image accessorySlotImage = new Image(accessorySlotTexture);

        characterTable.add(weaponSlotImage).size(64, 64).pad(5);
        dragAndDrop.addTarget(createEquipmentSlotTarget(weaponSlotImage));

        characterTable.add(shieldSlotImage).size(64, 64).pad(5);
        dragAndDrop.addTarget(createEquipmentSlotTarget(shieldSlotImage));

        characterTable.add(accessorySlotImage).size(64, 64).pad(5);
        dragAndDrop.addTarget(createEquipmentSlotTarget(accessorySlotImage));

        inventoryStage.addActor(characterTable);
        inventoryStage.addActor(inventoryTable);
    }

    // Helper method
    private DragAndDrop.Target createEquipmentSlotTarget(final Image slotImage) {
        return new DragAndDrop.Target(slotImage) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                return true;
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                Image draggedItem = (Image) payload.getObject();
                slotImage.setDrawable(draggedItem.getDrawable());  // Equip the item
            }
        };
    }

    public void render(float delta) {
        inventoryStage.act(delta);
        inventoryStage.draw();
    }

    public List<Item> getItems() {
        return items;
    }

    public Stage getStage() {
        return inventoryStage;  // Return the inventory stage to be processed
    }

    public void resize(int width, int height) {
        inventoryStage.getViewport().update(width, height, true);
    }

    public void dispose() {
        inventoryStage.dispose();
        weaponSlotTexture.dispose();
        shieldSlotTexture.dispose();
        accessorySlotTexture.dispose();
    }
}
