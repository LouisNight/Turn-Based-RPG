package io.github.louisnight.turnbasedrpg.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.louisnight.turnbasedrpg.TestRPG;
import io.github.louisnight.turnbasedrpg.entities.Player;

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


    // constructor with core/main argument
    public GameScreen(TestRPG testRPG) {
        this.parent = testRPG;

        // Initialize camera and viewport
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        // Load a tiled map
        map = new TmxMapLoader().load("Maps/Test_map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        // Set up the game world (optional for physics)
        world = new World(new Vector2(0, 0), true);

        // Stage for UI or entities
        stage = new Stage(new ScreenViewport(camera));

        player = new Player(camera.position.x, camera.position.y);

        batch = new SpriteBatch();

        Gdx.input.setInputProcessor(stage);
    }

    private void centerCameraOnMap() {
        // Get map properties
        float mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
        float mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);

        // Center the camera on the middle of the map
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
        batch.end();

        // update world (for physics, etc.)
        world.step(1/60f, 6, 2);

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
        player.update(delta, moveUp, moveDown, moveLeft, moveRight);
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
    }
}
