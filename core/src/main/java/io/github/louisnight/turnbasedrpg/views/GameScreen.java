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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.louisnight.turnbasedrpg.TestRPG;
import io.github.louisnight.turnbasedrpg.entities.Enemy;
import io.github.louisnight.turnbasedrpg.entities.EnemyFactory;
import io.github.louisnight.turnbasedrpg.entities.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class GameScreen implements Screen {

    private TestRPG parent; // storing orchestrator
    private Stage stage;
    private OrthographicCamera camera;
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
        camera.setToOrtho(false, 800, 600);

        // Load a tiled map
        map = new TmxMapLoader().load("Maps/Dungeon1Map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        // Set up the game world (optional for physics)
        world = new World(new Vector2(0, 0), true);

        // Stage for UI or entities
        stage = new Stage(new ScreenViewport(camera));

        player = new Player(camera.position.x, camera.position.y);

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
        enemies.add(EnemyFactory.createEnemy("skeleton", 200, 200));
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

        // Draw your screen here. "delta" is the time since last render in seconds.
        // Clear Screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        // Handle player input
        handleInput(delta);

        // camera following player
        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();

        // rendering map
        mapRenderer.setView(camera);
        mapRenderer.render();

        // Render the player
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        player.render(batch);

        for (Enemy enemy : enemies) {
            enemy.update(delta);
            enemy.render(batch);
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
       camera.viewportWidth = width;
       camera.viewportHeight = height;
       camera.update();
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
