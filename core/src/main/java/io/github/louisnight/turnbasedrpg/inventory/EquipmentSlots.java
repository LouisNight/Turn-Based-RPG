//package io.github.louisnight.turnbasedrpg.inventory;
//
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.scenes.scene2d.Stage;
//import com.badlogic.gdx.scenes.scene2d.ui.Image;
//import com.badlogic.gdx.scenes.scene2d.ui.Skin;
//import com.badlogic.gdx.scenes.scene2d.ui.Table;
//import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
//
//public class EquipmentSlots {
//    private Image weaponSlot;
//    private Image shieldSlot;
//    private DragAndDrop dragAndDrop;
//
//    public EquipmentSlots(Skin skin, Stage equipmentStage, DragAndDrop dragAndDrop) {
//        Table equipmentTable = new Table();
//        equipmentTable.top().right();
//        equipmentTable.setFillParent(true);
//
//        // Weapon slot
//        weaponSlot = new Image(new Texture("../assets/UI/weaponSlot.png"));
//        equipmentTable.add(weaponSlot).size(64, 64).pad(10);
//
//        // Shield slot
//        shieldSlot = new Image(new Texture("../assets/UI/shieldSlot.png"));
//        equipmentTable.add(shieldSlot).size(64, 64).pad(10);
//
//        // Add to stage
//        equipmentStage.addActor(equipmentTable);
//
//        // Set up drop target for the weapon slot
//        dragAndDrop.addTarget(new DragAndDrop.Target(weaponSlot) {
//            @Override
//            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
//                return true;  // Return true to allow drop
//            }
//
//            @Override
//            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
//                Image itemImage = (Image) payload.getObject();
//                System.out.println("Item dropped on weapon slot");
//                // Add logic for equipping the item here
//            }
//        });
//
//        // Set up drop target for the shield slot
//        dragAndDrop.addTarget(new DragAndDrop.Target(shieldSlot) {
//            @Override
//            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
//                return true;  // Allow drop
//            }
//
//            @Override
//            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
//                Image itemImage = (Image) payload.getObject();
//                System.out.println("Item dropped on shield slot");
//                // Add logic for equipping the item here
//            }
//        });
//    }
//
////    public void resize(int width, int height) {
////        equipmentStage.getViewport().update(width, height, true);
////    }
//
//    public void dispose() {
//        // Dispose textures
//    }
