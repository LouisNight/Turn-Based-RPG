package io.github.louisnight.turnbasedrpg.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.louisnight.turnbasedrpg.TestRPG;
import io.github.louisnight.turnbasedrpg.entities.Enemy;
import io.github.louisnight.turnbasedrpg.entities.EnemyFactory;
import io.github.louisnight.turnbasedrpg.entities.Player.ImplementPlayer;
import io.github.louisnight.turnbasedrpg.entities.Player.Player;
//import io.github.louisnight.turnbasedrpg.inventory.EquipmentSlots;
import io.github.louisnight.turnbasedrpg.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;


public class GameScreen implements Screen {

    private TestRPG parent; // storing orchestrator
    private Stage stage;
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
    private Skin skin;

    private Texture healthBarFrameTexture;
    private Texture healthBarRedTexture;
    private Image healthBarFrameImage;
    private Image healthBarRedImage;

    private Inventory inventory;
    private boolean isInventoryOpen = false;
    //private EquipmentSlots equipmentSlots;
    private DragAndDrop dragAndDrop;
    private Stage inventoryStage;

    // constructor with core/main argument
    public GameScreen(TestRPG testRPG) {
        this.parent = testRPG;

        uiStage = new Stage(new ScreenViewport());
        inventoryStage = new Stage(new ScreenViewport());

        dragAndDrop = new DragAndDrop();
        inventory = new Inventory(skin, inventoryStage);
        //equipmentSlots = new EquipmentSlots(skin, uiStage, dragAndDrop);

        healthBarFrameTexture = new Texture("../assets/UI/Health_01_16x16.png");
        healthBarRedTexture = new Texture("../assets/UI/Health_01_Bar01_16x16.png");

        enemies = new ArrayList<>();
        spawnEnemies();

        uiStage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));

        player = new ImplementPlayer(100,100);
        player.setMaxHealth(100);
        player.setHealth(100);

        batch = new SpriteBatch();

        Table uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.top().left();

        float healthBarWidth = 150;
        float healthBarHeight = 25;

        healthBarRedImage = new Image(healthBarRedTexture);
        healthBarFrameImage = new Image(healthBarFrameTexture);

        healthBarFrameImage.setSize(healthBarWidth, healthBarHeight);
        healthBarRedImage.setSize(healthBarWidth, healthBarHeight);

        Table healthBarTable = new Table();
        healthBarTable.top().left();
        healthBarTable.setFillParent(true);

        healthBarTable.stack(healthBarFrameImage, healthBarRedImage).width(healthBarWidth).height(healthBarHeight).pad(20).top().left();

        uiStage.addActor(healthBarTable);

        Gdx.input.setInputProcessor(uiStage);

        camera = new OrthographicCamera();
        viewport = new FitViewport(400, 300, camera);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float aspectRatio = screenWidth / screenHeight;

        //uiTable.setSize(screenWidth * 0.8f, screenHeight * 0.2f);


        map = new TmxMapLoader().load("Maps/Dungeon1Map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        world = new World(new Vector2(0, 0), true);

        stage = new Stage(new ScreenViewport(camera));
        loadCollisionRectangles();

        Gdx.input.setInputProcessor(stage);
    }


    private void loadCollisionRectangles() {

        collisionRectangles = new ArrayList<>();

        for (MapObject object : map.getLayers().get("Collision").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                collisionRectangles.add(rect);
                System.out.println("Loaded rectangle at: " + rect.x + ", " + rect.y);
            }
        }
    }

    private void spawnEnemies() {
        enemies.add(EnemyFactory.createEnemy("orc", 100, 100));
        enemies.add(EnemyFactory.createEnemy("skeleton", 200, 180));
    }
    private void centerCameraOnMap() {
        // Get map properties
        float mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
        float mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);

        // Center the camera in the middle of the map
        camera.position.set(mapWidth / 2f, mapHeight / 2f, 0);
        camera.update();
    }

    @Override
    public void show() {

        Gdx.input.setInputProcessor(stage);

        centerCameraOnMap();
    }

    @Override
    public void render(float delta) {

        // "delta" is the time since last render in seconds.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        // Handle player input
        handleInput(delta);

        Vector2 playerPosition = player.getPosition();

        // camera following player
        camera.position.set(playerPosition.x + camera.viewportWidth / 6, playerPosition.y + camera.viewportHeight / 6, 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        mapRenderer.setView(camera);
        mapRenderer.render();

        player.render(batch);

        for (Enemy enemy : enemies) {
            enemy.update(delta);
            enemy.render(batch);

            Rectangle enemyHitbox = enemy.getBoundingBox();
            if (player.getBoundingBox().overlaps(enemyHitbox)) {
                System.out.println("Collision detected with enemy!");
            }
        }
        batch.end();

        float healthPercentage = player.getHealth() / player.getMaxHealth();
        healthBarRedImage.setWidth(150 * healthPercentage);

        if (isInventoryOpen) {
            inventory.render(delta);
        } else {
            uiStage.act(delta);
            uiStage.draw();
            Gdx.input.setInputProcessor(uiStage);
        }

        // DEBUGGING COLLISION (PLAYER && RECTANGLES)
//        ShapeRenderer shapeRenderer = new ShapeRenderer();
//        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.RED);
//
//        Rectangle playerBoundingBox = player.getBoundingBox();
//        shapeRenderer.rect(playerBoundingBox.x, playerBoundingBox.y, playerBoundingBox.width, playerBoundingBox.height);
//        for (Rectangle rect : collisionRectangles) {
//            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
//        }
//        shapeRenderer.end();

        // update world (for physics, etc.)

        world.step(1/60f, 6, 2);
    }

    private void handleInput(float delta) {

        // Toggle inventory when "I" is pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            isInventoryOpen = !isInventoryOpen;  // Toggle inventory open/close state
        }

        // Handle player movement if inventory is not open
        if (!isInventoryOpen) {
            boolean moveUp = Gdx.input.isKeyPressed(Input.Keys.W);
            boolean moveDown = Gdx.input.isKeyPressed(Input.Keys.S);
            boolean moveLeft = Gdx.input.isKeyPressed(Input.Keys.A);
            boolean moveRight = Gdx.input.isKeyPressed(Input.Keys.D);

            player.update(delta, moveUp, moveDown, moveLeft, moveRight, collisionRectangles); // Assume you handle collisions elsewhere
        } else {
            Gdx.input.setInputProcessor(inventory.getStage());
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiStage.getViewport().update(width, height, true);
        inventory.resize(width, height);
    }


    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
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
