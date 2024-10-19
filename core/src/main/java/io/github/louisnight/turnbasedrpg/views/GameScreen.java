package io.github.louisnight.turnbasedrpg.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
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
import io.github.louisnight.turnbasedrpg.inventory.Inventory;

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
    private DragAndDrop dragAndDrop;
    private Stage inventoryStage;

    public GameScreen(TestRPG testRPG) {
        this.parent = testRPG;

        uiStage = new Stage(new ScreenViewport());
        inventoryStage = new Stage(new ScreenViewport());

        dragAndDrop = new DragAndDrop();
        inventory = new Inventory(skin, inventoryStage);

        healthBarFrameTexture = new Texture("../assets/UI/Health_01_16x16.png");
        healthBarRedTexture = new Texture("../assets/UI/Health_01_Bar01_16x16.png");

        enemies = new ArrayList<>();
        spawnEnemies();

        uiStage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));

        player = new ImplementPlayer(100, 100);
        player.setMaxHealth(100);
        player.setHealth(100);

        batch = new SpriteBatch();

        // Define target health bar size (adjust the target width as necessary)
        float targetHealthBarWidth = 200; // Adjust as needed
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

        Gdx.input.setInputProcessor(uiStage);

        camera = new OrthographicCamera();
        viewport = new FitViewport(400, 300, camera);

        map = new TmxMapLoader().load("Maps/Dungeon1Map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        world = new World(new Vector2(0, 0), true);

        stage = new Stage(new ScreenViewport(camera));
        loadCollisionRectangles();
    }

    private void loadCollisionRectangles() {
        collisionRectangles = new ArrayList<>();
        for (MapObject object : map.getLayers().get("Collision").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                collisionRectangles.add(rect);
            }
        }
    }

    private void spawnEnemies() {
        enemies.add(EnemyFactory.createEnemy("orc", 100, 100));
        enemies.add(EnemyFactory.createEnemy("skeleton", 200, 180));
    }

    private void centerCameraOnMap() {
        float mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
        float mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);
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
        player.render(batch);

        for (Enemy enemy : enemies) {
            enemy.update(delta);
            enemy.render(batch);
        }
        batch.end();

        // Render health bar
        float healthPercentage = player.getHealth() / player.getMaxHealth();
        healthBarRedImage.setWidth(healthBarFrameImage.getWidth() * healthPercentage);

        uiStage.act(delta);
        uiStage.draw();

        if (isInventoryOpen) {
            inventory.render(delta, batch);
        }

        world.step(1 / 60f, 6, 2);
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
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

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
