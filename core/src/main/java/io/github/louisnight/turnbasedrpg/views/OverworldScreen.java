package io.github.louisnight.turnbasedrpg.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.louisnight.turnbasedrpg.TestRPG;
import io.github.louisnight.turnbasedrpg.entities.Chest;
import io.github.louisnight.turnbasedrpg.entities.ChestManager;
import io.github.louisnight.turnbasedrpg.entities.Enemy;
import io.github.louisnight.turnbasedrpg.entities.Player.ImplementPlayer;
import io.github.louisnight.turnbasedrpg.entities.Player.Player;
import io.github.louisnight.turnbasedrpg.inventory.Inventory;
import io.github.louisnight.turnbasedrpg.ui.HealthBar;

import java.util.ArrayList;
import java.util.List;

public class OverworldScreen implements Screen {

    private TestRPG parent;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private World world;

    private SpriteBatch batch;
    private Player player;
    private ArrayList<Rectangle> collisionRectangles;
    private List<Enemy> enemies;
    private Stage uiStage;
    private Stage escMenuStage;
    private Skin skin;
    private HealthBar playerHealthBar;

    private Inventory inventory;
    private boolean isInventoryOpen = false;
    private boolean isEscMenuOpen = false;
    private EscMenuScreen escMenuScreen;
    private List<Chest> chests;
    private ChestManager chestManager;
    private Label coordinateLabel;
    private boolean debugMode = false;
    private Vector2 lastPlayerPosition;
    private Vector2 lastEnemyPosition;
    private ShapeRenderer shapeRenderer;
    private Rectangle screenTransitionTrigger;  // Invisible trigger


    public OverworldScreen(TestRPG testRPG) {
        this.parent = testRPG;

        collisionRectangles = new ArrayList<>();

        // Initialize camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(400, 300, camera);

        // Initialize player
        player = new ImplementPlayer("Player1", 464, 752);
        player.setMaxHealth(100);
        player.setHealth(100);



        // Center the camera on the player
        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();

        // Initialize the screen transition trigger (invisible rectangle)
        screenTransitionTrigger = new Rectangle(1568, 864, 32, 16); // Adjust the position and size as needed

        // Initialize batch and ShapeRenderer
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Initialize enemies
        enemies = new ArrayList<>();
        spawnEnemies();

        map = new TmxMapLoader().load("Maps/OverworldMap.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        // Initialize world
        world = new World(new Vector2(0, 0), true);

        // Initialize UI and health bar
        skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));
        uiStage = new Stage(new ScreenViewport());
        inventory = new Inventory(skin, uiStage);

        Texture healthBarFrameTexture = new Texture("../assets/UI/Health_01_16x16.png");
        Texture healthBarRedTexture = new Texture("../assets/UI/Health_01_Bar01_16x16.png");

        playerHealthBar = new HealthBar(new Image(healthBarFrameTexture), new Image(healthBarRedTexture));
        playerHealthBar.setSize(150, 30); // Regular size during gameplay
        playerHealthBar.setPosition(50, Gdx.graphics.getHeight() - playerHealthBar.getHeight() - 50); // Position at the top-left corner
        playerHealthBar.addToStage(uiStage);
        System.out.println("Player health bar added to the UI stage.");

        // Initialize chests
        chestManager = new ChestManager("chests.json");
        chests = chestManager.getChestsForArea("Overworld");

        // Initialize ESC menu stage
        escMenuScreen = new EscMenuScreen(parent, player);
        escMenuStage = escMenuScreen.getStage();

        coordinateLabel = new Label("", skin); // Use the same skin as other UI elements
        coordinateLabel.setColor(1,1,1,1);// Position it near the bottom-left corner
        coordinateLabel.setFontScale(1f); // Optionally adjust the font size
        uiStage.addActor(coordinateLabel); // Add it to the stage

        // Set input processor
        Gdx.input.setInputProcessor(uiStage);

        // Load collision layer
        loadCollisionLayer();
    }

    private void setupEscMenu() {
        Table escMenuTable = new Table();
        escMenuTable.setFillParent(true);

        Label titleLabel = new Label("Paused", skin);
        escMenuTable.add(titleLabel).center().pad(10);
        escMenuTable.row();

        escMenuStage.addActor(escMenuTable);
    }

    private ArrayList<Rectangle> getCollisionRectangles() {
        return collisionRectangles;
    }

    private void spawnEnemies() {
    }

    public Vector2 getPlayerPosition() {
        return new Vector2(player.getPosition());
    }

    public void restorePlayerPosition(Vector2 position) {
        if (position != null) {
            player.setPosition(position.x, position.y);
        }
    }

    public void returnToOverworldWithWin() {
        System.out.println("Player won the combat. Returning to overworld.");

        // Restore player and enemy positions
        if (lastPlayerPosition != null) {
            player.setPosition(lastPlayerPosition.x, lastPlayerPosition.y);
        }
        if (lastEnemyPosition != null) {
            for (Enemy enemy : enemies) {
                if (enemy.getPosition().epsilonEquals(lastEnemyPosition)) {
                    enemies.remove(enemy);
                    break;
                }
            }
        }

        if (!uiStage.getActors().contains(playerHealthBar.getFrameImage(), true)) {
            playerHealthBar.addToStage(uiStage);
        }

        resetUIState();
    }

    public void transitionToNewMap(String mapFilePath, Vector2 spawnPosition) {
        System.out.println("Transitioning to new map: " + mapFilePath);

        if (map != null) {
            map.dispose();
        }
        map = new TmxMapLoader().load(mapFilePath);
        mapRenderer.setMap(map);

        collisionRectangles.clear();
        loadCollisionLayer();

        player.setPosition(spawnPosition.x, spawnPosition.y);
        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();

        resetUIState();
    }


    public void returnToOverworldWithLoss() {
        System.out.println("Player lost the combat. Returning to last save point.");

        if (lastPlayerPosition != null) {
            player.setPosition(lastPlayerPosition.x, lastPlayerPosition.y);
        }

        if (!uiStage.getActors().contains(playerHealthBar.getFrameImage(), true)) {
            playerHealthBar.addToStage(uiStage);
        }
       resetUIState();
    }

    private void resetUIState() {
        isEscMenuOpen = false;
        isInventoryOpen = false;
        Gdx.input.setInputProcessor(uiStage);
        parent.setScreen(this);
    }

    public void toggleEscMenu() {
        if (isEscMenuOpen) {
            isEscMenuOpen = false;
            Gdx.input.setInputProcessor(uiStage);
        } else {
            isEscMenuOpen = true;
            Gdx.input.setInputProcessor(escMenuStage);
        }
    }

    private void loadCollisionLayer() {
        TiledMapTileLayer collisionLayer = (TiledMapTileLayer) map.getLayers().get("Collision");
        if (collisionLayer == null) return;

        for (int x = 0; x < collisionLayer.getWidth(); x++) {
            for (int y = 0; y < collisionLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = collisionLayer.getCell(x, y);
                if (cell != null) {
                    float tileWidth = collisionLayer.getTileWidth();
                    float tileHeight = collisionLayer.getTileHeight();
                    Rectangle rect = new Rectangle(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
                    collisionRectangles.add(rect);
                }
            }
        }
    }

    private void checkCombatTriggers() {
        for (Enemy enemy : enemies) {
            if (player.getBoundingBox().overlaps(enemy.getBoundingBox())) {
                System.out.println("Combat triggered with enemy!");
                transitionToCombatScreen(enemy);
                break;
            }
        }
    }

    private void transitionToCombatScreen(Enemy enemy) {
        lastPlayerPosition = new Vector2(player.getPosition());
        lastEnemyPosition = new Vector2(enemy.getPosition());

        List<Enemy> combatEnemies = new ArrayList<>();
        combatEnemies.add(enemy);

        parent.setScreen(new CombatScreen(parent, player, combatEnemies, playerHealthBar));
    }

    private void handleInput(float delta) {
        // Toggle ESC menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isEscMenuOpen = !isEscMenuOpen;
            isInventoryOpen = false;
            Gdx.input.setInputProcessor(isEscMenuOpen ? escMenuScreen.getStage() : uiStage);
            System.out.println("Input Processor Set to: " + (isEscMenuOpen ? "ESC Menu Stage" : "UI Stage"));
            return; // Exit early to avoid other inputs being processed
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I) && !isEscMenuOpen) {
            isInventoryOpen = !isInventoryOpen;
            if (isInventoryOpen) {
                inventory.show();
                Gdx.input.setInputProcessor(inventory.getStage());
            } else {
                inventory.hide();
                Gdx.input.setInputProcessor(uiStage);
            }
            return;
        }

        if (!isInventoryOpen && !isEscMenuOpen) {
            boolean moveUp = Gdx.input.isKeyPressed(Input.Keys.W);
            boolean moveDown = Gdx.input.isKeyPressed(Input.Keys.S);
            boolean moveLeft = Gdx.input.isKeyPressed(Input.Keys.A);
            boolean moveRight = Gdx.input.isKeyPressed(Input.Keys.D);

            player.update(delta, moveUp, moveDown, moveLeft, moveRight, collisionRectangles);
        }
    }




    @Override
    public void render(float delta) {
        if (isEscMenuOpen) {
            escMenuStage.act(delta);
            escMenuStage.draw();
        } else {
            handleInput(delta);

            if (isInventoryOpen) {
                inventory.handleInput();
            }

            camera.position.set(player.getPosition().x, player.getPosition().y, 0);
            camera.update();

            uiStage.getViewport().apply();
            playerHealthBar.update(player.getHealth(), player.getMaxHealth());

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.setProjectionMatrix(camera.combined);
            mapRenderer.setView(camera);
            mapRenderer.render();

            batch.begin();

            if (isInventoryOpen) {
                inventory.renderOverlay(batch, camera);
                new Texture(Gdx.files.internal("UI/inv_backdrop.png"));
            }

            for (Chest chest : chests) {
                chest.render(batch);
            }

            player.render(batch);

            for (Enemy enemy : enemies) {
                enemy.update(delta, collisionRectangles);
                enemy.render(batch);
            }
            batch.end();

            checkCombatTriggers();
        }


        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            debugMode = !debugMode;
        }

        if (debugMode) {
            ShapeRenderer shapeRenderer = new ShapeRenderer();
            shapeRenderer.setProjectionMatrix(camera.combined);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);

            Rectangle playerBoundingBox = player.getBoundingBox();
            shapeRenderer.rect(playerBoundingBox.x, playerBoundingBox.y, playerBoundingBox.width, playerBoundingBox.height);

            for (Rectangle rect : collisionRectangles) {
                shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
            }

            shapeRenderer.setColor(Color.BLUE);

            for (Enemy enemy : enemies) {
                Rectangle enemyBoundingBox = enemy.getBoundingBox();
                shapeRenderer.rect(enemyBoundingBox.x, enemyBoundingBox.y, enemyBoundingBox.width, enemyBoundingBox.height);
            }
            shapeRenderer.end();

            Vector2 playerPos = player.getPosition();
            coordinateLabel.setText("Player: (" + (int) playerPos.x + ", " + (int) playerPos.y + ")");
            coordinateLabel.setPosition(10, uiStage.getViewport().getWorldHeight() - 55);
            coordinateLabel.setVisible(true);
        } else {
            coordinateLabel.setVisible(false);
        }

        if (isEscMenuOpen) {
            escMenuScreen.render(delta);
        } else if (isInventoryOpen) {
            inventory.render(delta);
        } else {
            uiStage.act(delta);
            uiStage.draw();
        }

        world.step(1 / 60f, 6, 2);
        update(delta);
    }


    public void update(float delta) {
        for (Chest chest : chests) {
            if (!chest.isOpened() && player.getBoundingBox().overlaps(new Rectangle(chest.getPosition().x, chest.getPosition().y, 32, 32))) {
                chest.open();
                inventory.addItems(chest.getItems());
                System.out.println("Opened chest and added items: " + chest.getItems());
            }
        }

        if (player.getBoundingBox().overlaps(screenTransitionTrigger)) {

            parent.setScreen(new DungeonScreen(parent));
        }

    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiStage.getViewport().update(width, height, true);
        escMenuStage.getViewport().update(width, height, true);

        inventory.resize(width, height);

        playerHealthBar.setPosition(10, height - 40);
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        uiStage.dispose();

        escMenuStage.dispose();
        world.dispose();
        player.dispose();
        for (Enemy enemy : enemies) enemy.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void show() {
        Gdx.input.setInputProcessor(uiStage);
    }
}
