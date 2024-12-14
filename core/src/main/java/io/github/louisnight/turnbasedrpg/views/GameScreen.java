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
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.louisnight.turnbasedrpg.TestRPG;
import io.github.louisnight.turnbasedrpg.entities.Chest;
import io.github.louisnight.turnbasedrpg.entities.ChestManager;
import io.github.louisnight.turnbasedrpg.entities.Enemy;
import io.github.louisnight.turnbasedrpg.entities.EnemyFactory;
import io.github.louisnight.turnbasedrpg.entities.Player.ImplementPlayer;
import io.github.louisnight.turnbasedrpg.entities.Player.Player;
import io.github.louisnight.turnbasedrpg.inventory.Inventory;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    private TestRPG parent;
    private Stage stage;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private World world;
    private ScreenTransition screenTransition;
    private boolean switchToCombat = false;

    private SpriteBatch batch;
    private Player player;
    private ArrayList<Rectangle> collisionRectangles;
    private List<Enemy> enemies;
    private Stage uiStage;
    private Skin skin;
    private Enemy collidedEnemy;

    private Texture healthBarFrameTexture;
    private Texture healthBarRedTexture;
    private Image healthBarFrameImage;
    private Image healthBarRedImage;

    private Inventory inventory;
    private boolean isInventoryOpen = false;

    private List<Chest> chests;
    private ChestManager chestManager;

    private boolean debugMode = false;

    public GameScreen(TestRPG testRPG) {
        this.parent = testRPG;

        screenTransition = new ScreenTransition(1f);

        collisionRectangles = new ArrayList<>();

        uiStage = new Stage(new FitViewport(400, 300)); // UI Stage for overlay
        skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));
        inventory = new Inventory(skin, uiStage);

        healthBarFrameTexture = new Texture("../assets/UI/Health_01_16x16.png");
        healthBarRedTexture = new Texture("../assets/UI/Health_01_Bar01_16x16.png");

        enemies = new ArrayList<>();
        spawnEnemies();

        player = new ImplementPlayer(400, 650);

        player.setMaxHealth(100);
        player.setHealth(100);

        batch = new SpriteBatch();

        // Health bar setup
        float targetHealthBarWidth = 100;
        float targetHealthBarHeight = healthBarFrameTexture.getHeight() * (targetHealthBarWidth / healthBarFrameTexture.getWidth());

        healthBarRedImage = new Image(healthBarRedTexture);
        healthBarFrameImage = new Image(healthBarFrameTexture);

        healthBarFrameImage.setSize(targetHealthBarWidth, targetHealthBarHeight);
        healthBarRedImage.setSize(targetHealthBarWidth, targetHealthBarHeight);

        Table healthBarTable = new Table();
        healthBarTable.top().left();
        healthBarTable.setFillParent(true);

        healthBarTable.stack(healthBarFrameImage, healthBarRedImage)
            .width(targetHealthBarWidth)
            .height(targetHealthBarHeight)
            .pad(20)
            .top()
            .left();

        uiStage.addActor(healthBarTable);

        // Camera setup
        camera = new OrthographicCamera();
        viewport = new FitViewport(400, 300, camera);

        // Map setup
        map = new TmxMapLoader().load("Maps/OverworldMap.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        loadCollisionLayer();

        world = new World(new Vector2(0, 0), true);

        stage = new Stage(new ScreenViewport(camera));

        chestManager = new ChestManager("chests.json");

        // Load chests for the current area
        chests = chestManager.getChestsForArea("Dungeon1");
    }



    private void loadCollisionLayer () {
        // Access the collision layer by name
        TiledMapTileLayer collisionLayer = (TiledMapTileLayer) map.getLayers().get("Collision");

        if (collisionLayer == null) {
            System.out.println("No collision layer found");
            return;
        }

        // Loop through the tiles in the collision layer
        for (int x = 0; x < collisionLayer.getWidth(); x++) {
            for (int y = 0; y < collisionLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = collisionLayer.getCell(x, y);
                if (cell != null) {
                    // Get the tile's rectangular bounds (assuming tile width and height are fixed for simplicity)
                    float tileWidth = collisionLayer.getTileWidth();
                    float tileHeight = collisionLayer.getTileHeight();

                    Rectangle rect = new Rectangle(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
                    collisionRectangles.add(rect);

                    System.out.println("Loaded collision tile at: " + rect.x + ", " + rect.y);
                }
            }
        }
    }

    private void spawnEnemies() {
        enemies.add(EnemyFactory.createEnemy("orc", 2100, 100));
        enemies.add(EnemyFactory.createEnemy("heavyorc", 2100, 180));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        centerCameraOnMap();
    }

    private void centerCameraOnMap() {
        float mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
        float mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);
        camera.position.set(mapWidth / 2f, mapHeight / 2f, 0);
        camera.update();
    }

    private void handleInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            isInventoryOpen = !isInventoryOpen;
        }

        if (!isInventoryOpen) {
            boolean moveUp = Gdx.input.isKeyPressed(Input.Keys.W);
            boolean moveDown = Gdx.input.isKeyPressed(Input.Keys.S);
            boolean moveLeft = Gdx.input.isKeyPressed(Input.Keys.A);
            boolean moveRight = Gdx.input.isKeyPressed(Input.Keys.D);
            player.update(delta, moveUp, moveDown, moveLeft, moveRight, collisionRectangles);
        } else {
            Gdx.input.setInputProcessor(inventory.getStage());
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput(delta);

        Vector2 playerPosition = player.getPosition();
        camera.position.set(playerPosition.x + camera.viewportWidth / 6, playerPosition.y + camera.viewportHeight / 6, 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        mapRenderer.setView(camera);
        mapRenderer.render();

        for (Chest chest : chests) {
            chest.render(batch);
        }

        for (Enemy enemy : enemies) {
            enemy.update(delta);
            enemy.render(batch);
        }

        player.render(batch);

        batch.end();

        batch.begin();
        screenTransition.render(batch);
        batch.end();

        // Render health bar
        float healthPercentage = player.getHealth() / player.getMaxHealth();
        healthBarRedImage.setWidth(healthBarFrameImage.getWidth() * healthPercentage);

        uiStage.act(delta);
        uiStage.draw();

        if (isInventoryOpen) {
            // Render the inventory backdrop first
            inventory.renderOverlay(batch, camera);

            // Render the UI elements on top of the backdrop
            uiStage.act(delta);
            uiStage.draw();

            // Handle input for the inventory
            inventory.handleInput();
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

            world.step(1 / 60f, 6, 2);
        }
    }


    public void update(float delta) {
        screenTransition.update(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {  // Example: 'I' key to toggle inventory
            inventory.toggleVisibility();
        }
        for (Enemy enemy : enemies) {
            if (player.getHitbox().overlaps(enemy.getHitbox()) && !screenTransition.isTransitioning()) {
                screenTransition.startFadeOut();
                switchToCombat = true;
                collidedEnemy = enemy;
            }
        }

        if (!screenTransition.isFadingOut() && !screenTransition.isFadingIn() && switchToCombat) {
            switchToCombatScreen();
            screenTransition.startFadeIn();
            switchToCombat = false;
        }

        for (Chest chest : chests) {
            if (!chest.isOpened() && player.getBoundingBox().overlaps(new Rectangle(chest.getPosition().x, chest.getPosition().y, 32, 32))) {
                chest.open();
                inventory.addItems(chest.getItems());
                System.out.println("Opened chest and added items: " + chest.getItems());
            }
        }
    }

    public void returnToOverworldWithWin(Enemy defeatedEnemy) {
        enemies.remove(defeatedEnemy);
        parent.setScreen(this);
    }

    public void returnToOverworldWithLoss() {
        System.out.println("Player Defeated - HANDLE DEFEAT LOGIC");
        parent.setScreen(this);
    }

    private void switchToCombatScreen() {
        parent.setScreen(new CombatScreen(parent, player, collidedEnemy));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        world.dispose();
        stage.dispose();
        player.dispose();
        batch.dispose();
        uiStage.dispose();
        skin.dispose();
        healthBarRedTexture.dispose();
        healthBarFrameTexture.dispose();
        inventory.dispose();

        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
    }
}
