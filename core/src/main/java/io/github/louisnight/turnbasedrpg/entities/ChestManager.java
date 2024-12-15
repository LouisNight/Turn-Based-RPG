package io.github.louisnight.turnbasedrpg.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.*;
import java.util.ArrayList;
import java.util.List;

public class ChestManager {
    private ObjectMap<String, Array<Chest>> areaChests;

    public ChestManager(String jsonFilePath) {
        areaChests = new ObjectMap<>();
        loadChestsFromJson(jsonFilePath);
    }

    private void loadChestsFromJson(String jsonFilePath) {
        Json json = new Json();
        JsonValue root;

        try {
            root = new JsonReader().parse(Gdx.files.internal(jsonFilePath)); // Read the JSON file
        } catch (Exception e) {
            Gdx.app.error("ChestManager", "Failed to load chest JSON: " + jsonFilePath, e);
            return;
        }

        JsonValue areasNode = root.get("areas");
        if (areasNode == null) {
            Gdx.app.error("ChestManager", "No 'areas' node found in JSON.");
            return;
        }

        for (JsonValue areaNode : areasNode) {
            String areaName = areaNode.name(); // e.g., "Dungeon1"
            Array<Chest> chestList = new Array<>();

            for (JsonValue chestNode : areaNode) {
                try {
                    float x = chestNode.getFloat("x");
                    float y = chestNode.getFloat("y");
                    Array<String> items = json.readValue(Array.class, String.class, chestNode.get("items"));

                    // Create the Chest object
                    Chest chest = new Chest(x, y, items);
                    chestList.add(chest);
                } catch (Exception e) {
                    Gdx.app.error("ChestManager", "Error parsing chest in area: " + areaName, e);
                }
            }

            // Add the parsed chests to the area map
            areaChests.put(areaName, chestList);
        }
    }

    public List<Chest> getChestsForArea(String area) {
        Array<Chest> chests = areaChests.get(area, new Array<>());

        // Convert LibGDX Array<Chest> to a Java List<Chest>
        List<Chest> chestList = new ArrayList<>();
        for (Chest chest : chests) {
            chestList.add(chest);
        }

        return chestList;
    }
}
