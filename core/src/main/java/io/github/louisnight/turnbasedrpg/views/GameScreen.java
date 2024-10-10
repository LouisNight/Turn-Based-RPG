package io.github.louisnight.turnbasedrpg.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.louisnight.turnbasedrpg.TestRPG;
import io.github.louisnight.turnbasedrpg.entities.Enemy;
import io.github.louisnight.turnbasedrpg.entities.EnemyFactory;
import io.github.louisnight.turnbasedrpg.entities.Player;

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

    private Label playerHealthLabel;
    private TextButton inventoryButton;

    // constructor with core/main argument
    public GameScreen(TestRPG testRPG) {
        this.parent = testRPG;
        enemies = new ArrayList<>();
        spawnEnemies();

        uiStage = new Stage(new ScreenViewport());

        skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));

        playerHealthLabel = new Label("Health: 100", skin);
        inventoryButton = new TextButton("Inventory", skin);

        inventoryButton.getLabel().setFontScale(0.5f);

        inventoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
           System.out.println("Inventory Button Pressed");
            }
        });

        Table uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.top().left();

        uiTable.add(playerHealthLabel).pad(10).left();
        uiTable.row();
        uiTable.add(inventoryButton).width(200).height(50).pad(10).left();

        uiStage.addActor(uiTable);

        Gdx.input.setInputProcessor(uiStage);

        // Initialize camera and viewport
        camera = new OrthographicCamera();

        viewport = new FitViewport(400, 300, camera);
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float aspectRatio = screenWidth / screenHeight;

        uiTable.setSize(screenWidth * 0.8f, screenHeight * 0.2f);

        //camera.setToOrtho(false, 400, 400 / aspectRatio);

        // Load a tiled map
        map = new TmxMapLoader().load("Maps/Dungeon1Map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        // Set up the game world (optional for physics)
        world = new World(new Vector2(0, 0), true);

        // Stage for UI or entities
        stage = new Stage(new ScreenViewport(camera));

        player = new Player(400, 300);

        batch = new SpriteBatch();

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
        // Prepare your screen here.
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
        // rendering map
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
        // DEBUGGING COLLISION (PLAYER && RECTANGLES)
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        Rectangle playerBoundingBox = player.getBoundingBox();
        shapeRenderer.rect(playerBoundingBox.x, playerBoundingBox.y, playerBoundingBox.width, playerBoundingBox.height);
        for (Rectangle rect : collisionRectangles) {
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }
        shapeRenderer.end();

        batch.end();

        // update world (for physics, etc.)
        world.step(1/60f, 6, 2);

        playerHealthLabel.setText("Health: " + player.getHealth());

        uiStage.act(delta);
        uiStage.draw();

        // Draw the stage (UI/Actors)
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private void handleInput(float delta) {
        boolean moveUp = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean moveDown = Gdx.input.isKeyPressed(Input.Keys.S);
        boolean moveLeft = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean moveRight = Gdx.input.isKeyPressed(Input.Keys.D);

        // Update the player's position based on input
        player.update(delta, moveUp, moveDown, moveLeft, moveRight, collisionRectangles);
        }

    @Override
    public void resize(int width, int height) {
//        float aspectRatio = (float) width / (float) height;
//
//       camera.viewportWidth = 400;
//       camera.viewportHeight = 400 / aspectRatio;
//       camera.update();
        viewport.update(width, height);
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

        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
    }
}
