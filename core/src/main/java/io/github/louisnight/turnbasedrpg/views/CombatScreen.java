package io.github.louisnight.turnbasedrpg.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.louisnight.turnbasedrpg.TestRPG;
import io.github.louisnight.turnbasedrpg.entities.Enemy;
import io.github.louisnight.turnbasedrpg.entities.EnemyState;
import io.github.louisnight.turnbasedrpg.entities.Player.Player;

public class CombatScreen implements Screen {

    private SpriteBatch batch;
    private Player player;
    private Enemy enemy;
    private Texture combatBackground;  // Background texture for combat screen
    private Texture playerTexture;  // Texture for player sprite
    private Texture enemyTexture;  // Texture for enemy sprite

    private boolean playerTurn;
    private boolean isGameOver;
    private boolean playerWon;
    private boolean enemyWon;
    private float stateTime;

    private Stage stage;
    private Skin skin;
    private TextButton attackButton;
    private TextButton defendButton;

    private TestRPG parent;

    public CombatScreen(TestRPG parent, Player player, Enemy enemy) {
        this.parent = parent;
        this.player = player;
        this.enemy = enemy;

        player.loadCombatAssets();
        enemy.loadCombatAssets();
        stateTime = 0f;
        batch = new SpriteBatch();

        // Load combat background
        combatBackground = new Texture("../assets/Maps/dungeon_combat.png");

        playerTurn = true;
        isGameOver = false;
        playerWon = false;

        // Set up UI stage
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));  // Load UI skin

        // Create buttons for player actions
        attackButton = new TextButton("Attack", skin);
        defendButton = new TextButton("Defend", skin);

        // Add listeners for button clicks
        attackButton.addListener(event -> {
            if (playerTurn) {
                playerAttack();
                playerTurn = false;
            }
            return true;
        });

        defendButton.addListener(event -> {
            if (playerTurn) {
                playerDefend();
                playerTurn = false;
            }
            return true;
        });

        // Layout the buttons using a table
        Table table = new Table();
        table.bottom().pad(10);
        table.setFillParent(true);
        table.add(attackButton).padRight(10);
        table.add(defendButton).padLeft(10);

        // Add the table to the stage
        stage.addActor(table);

        // Set input processor to the stage for UI
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
       stateTime += delta;

        batch.begin();

        batch.draw(combatBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        TextureRegion playerFrame;
        switch (player.getState()) {
            case ATTACKING:
                playerFrame = player.getAttackAnimation().getKeyFrame(stateTime, true);
                break;
            case HURT:
                playerFrame = player.getHurtAnimation().getKeyFrame(stateTime, true);
                break;
            case DEAD:
                playerFrame = player.getDeathAnimation().getKeyFrame(stateTime, false);  // Don't loop death animation
                break;
            case IDLE:
            default:
                playerFrame = player.getIdleAnimation().getKeyFrame(stateTime, true);
                break;
        }
        batch.draw(playerFrame, 160, 180);  // Draw the player frame

        TextureRegion enemyFrame;
        switch (enemy.getState()) {
            case ATTACKING:
                enemyFrame = enemy.getAttackAnimation().getKeyFrame(stateTime, true);
                break;
            case HURT:
                enemyFrame = enemy.getHurtAnimation().getKeyFrame(stateTime, true);
                break;
            case DEAD:
                enemyFrame = enemy.getDeathAnimation().getKeyFrame(stateTime, false);  // Play once, not loop
                break;
            case IDLE:
            default:
                enemyFrame = enemy.getIdleAnimation().getKeyFrame(stateTime, true);
                break;
        }
        batch.draw(enemyFrame, 180, 180);

        batch.end();

        stage.act(delta);
        stage.draw();

        if (!isGameOver) {
            if (!playerTurn) {
                enemyTurn(delta);
            }
        } else {
            endCombat();
        }
    }


    public void playerAttack() {
        System.out.println("Player attacks!");

        // Player hits the enemy
        enemy.setHealth(enemy.getHealth() - 20f);
        enemy.setState(EnemyState.HURT);  // Set the state to HURT when hit

        if (enemy.getHealth() <= 0) {
            enemy.setState(EnemyState.DEAD);  // Set to DEAD if the enemy's health reaches 0
            isGameOver = true;
            playerWon = true;
            System.out.println("Enemy defeated!");
        }
    }

    private void playerDefend() {
        System.out.println("Player defends!");

        playerTurn = false;
    }

    private void enemyTurn(float delta) {
        System.out.println("Enemy's Turn");

        // Simulate a simple attack
        enemyAttack();

        // Switch back to the player's turn
        playerTurn = true;
    }

    private void enemyAttack() {
        System.out.println("Enemy attacks!");

        // Set the state to ATTACKING when the enemy attacks
        enemy.setState(EnemyState.ATTACKING);

        // Example: Reduce player health
        player.setHealth(player.getHealth() - 15);

        // Check if the player is defeated
        if (player.getHealth() <= 0) {
            enemy.setState(EnemyState.IDLE);  // Reset to idle after attack
            isGameOver = true;
            playerWon = false;
            System.out.println("Player defeated!");
        } else {
            enemy.setState(EnemyState.IDLE);  // Reset to idle after attack
        }
    }

    private void endCombat() {
        System.out.println("Combat is over");

        if (playerWon) {
            parent.returnToOverworldWithWin(enemy);
        } else {
            parent.returnToOverworldWithLoss();
        }
    }

    @Override
    public void show() {}

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        combatBackground.dispose();
    }
}
