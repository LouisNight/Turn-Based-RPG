package io.github.louisnight.turnbasedrpg.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.louisnight.turnbasedrpg.TestRPG;
import io.github.louisnight.turnbasedrpg.entities.Enemy;
import io.github.louisnight.turnbasedrpg.entities.EnemyState;
import io.github.louisnight.turnbasedrpg.entities.Player.Player;
import io.github.louisnight.turnbasedrpg.entities.Player.PlayerState;
import io.github.louisnight.turnbasedrpg.ui.HealthBar;

import java.util.ArrayList;
import java.util.List;

public class CombatScreen implements Screen {

    private SpriteBatch batch;
    private Player player;
    private List<Enemy> enemies;
    private Texture combatBackground;

    private boolean playerTurn;
    private boolean isGameOver;
    private boolean playerWon;

    private float playerStateTime = 0f;
    private float enemyStateTime = 0f;
    private float actionDelayTimer = 0f;
    private float turnDelayTimer = 0f;
    private final float ACTION_DELAY = 1.5f; // Delay between actions
    private final float TURN_DELAY = 2f;    // Additional delay between turns

    private HealthBar playerHealthBar;
    private List<HealthBar> enemyHealthBars = new ArrayList<>();
    private Stage stage;
    private Skin skin;
    private TextButton attackButton;
    private TextButton defendButton;
    private boolean isCombatOver;

    private TestRPG parent;

    public CombatScreen(TestRPG parent, Player player, List<Enemy> enemies, HealthBar playerHealthBar) {
        this.parent = parent;
        this.player = player;
        this.enemies = enemies;
        this.isCombatOver = false;

        this.playerHealthBar = playerHealthBar;

        playerHealthBar.setPosition(50, Gdx.graphics.getHeight() - playerHealthBar.getHeight() - 50);
        for (Enemy enemy : enemies) {
            HealthBar enemyHealthBar = new HealthBar(
                new Image(new Texture("../assets/UI/Health_01_16x16.png")),
                new Image(new Texture("../assets/UI/Health_01_Bar01_16x16.png"))
            );
            enemyHealthBar.setPosition(Gdx.graphics.getWidth() - enemyHealthBar.getWidth() - 150, Gdx.graphics.getHeight() - enemyHealthBar.getHeight() - 80);
            enemyHealthBars.add(enemyHealthBar);
            enemyHealthBar.setSize(playerHealthBar.getWidth(), playerHealthBar.getHeight());
        }

        // Set up the stage and add health bars to it
        stage = new Stage(new ScreenViewport());
        playerHealthBar.addToStage(stage);
        for (HealthBar enemyHealthBar : enemyHealthBars) {
            enemyHealthBar.addToStage(stage);
        }

        // Initialize the batch
        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));


        player.loadCombatAssets();
        for (Enemy enemy : enemies) {
            enemy.loadCombatAssets();
        }

        combatBackground = new Texture("../assets/Maps/dungeon_combat.png");

        attackButton = new TextButton("Attack", skin);
        defendButton = new TextButton("Defend", skin);

        attackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (playerTurn && !isGameOver && turnDelayTimer >= TURN_DELAY) {
                    playerAttack(enemies.get(0));
                }
            }
        });

        defendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (playerTurn && !isGameOver && turnDelayTimer >= TURN_DELAY) {
                    playerDefend();
                }
            }
        });

        Table table = new Table();
        table.bottom().pad(10);
        table.setFillParent(true);
        table.add(attackButton).padRight(10);
        table.add(defendButton).padLeft(10);
        stage.addActor(table);

        playerTurn = true;
        isGameOver = false;
        playerWon = false;

        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        playerStateTime += delta;
        enemyStateTime += delta;
        actionDelayTimer += delta;
        turnDelayTimer += delta;

        playerHealthBar.update(player.getHealth(), player.getMaxHealth());
        for (int i = 0; i < enemies.size(); i++) {
            HealthBar healthBar = enemyHealthBars.get(i);
            Enemy enemy = enemies.get(i);
            healthBar.update(enemy.getCurrentHealth(), enemy.getMaxHealth());
        }

        batch.begin();
        batch.draw(combatBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Only handle first enemy for simplicity

        float playerX = 100; // Adjust these values to where you want the player to be on the screen
        float playerY = 150;
        batch.draw(getPlayerFrame(), playerX, playerY);

        // Draw enemy sprite at a fixed position (e.g., bottom right of the screen)
        float enemyX = Gdx.graphics.getWidth() - 300; // Adjust value to place enemy near the right edge
        float enemyY = 150;
        batch.draw(getEnemyFrame(enemies.get(0)), enemyX, enemyY);

        batch.end();

        stage.act(delta);
        stage.draw();

        if (!isGameOver) {
            if (playerTurn) {
                // Wait for the player to complete their action
                if (player.getState() == PlayerState.IDLE && turnDelayTimer >= TURN_DELAY) {
                    // Player input is required for the next action
                    stage.act(delta);
                    stage.draw();
                }
            } else {
                // Enemy's turn
                if (actionDelayTimer >= ACTION_DELAY && enemyStateTime >= TURN_DELAY) {
                    enemyTurn();
                }
            }
        } else {
            endCombat();
        }

    }

    private void playerAttack(Enemy targetEnemy) {
        System.out.println("Player attacks!");
        player.setState(PlayerState.ATTACKING);
        playerStateTime = 0f;
        actionDelayTimer = 0f;
        targetEnemy.setHealth(targetEnemy.getCurrentHealth() - 20f);
        targetEnemy.setState(EnemyState.HURT);

        if (targetEnemy.getCurrentHealth() <= 0) {
            targetEnemy.setState(EnemyState.DEAD);
            actionDelayTimer = 0f;
            System.out.println("Enemy defeated!");
            isGameOver = true;
            playerWon = true;
        }

        playerTurn = false;
        turnDelayTimer = 0f;
    }

    private void playerDefend() {
        System.out.println("Player defends!");
        turnDelayTimer = 0f;
        playerTurn = false; // End player's turn
    }

    private void enemyTurn() {
        System.out.println("Enemy's Turn");

        for (Enemy enemy : enemies) {
            if (enemy.getState() == EnemyState.IDLE) {
                enemyAttack(enemy); // Target the first available enemy that is idle
                break;
            }
        }
    }

    private void enemyAttack(Enemy attackingEnemy) {
        System.out.println("Enemy attacks!");

        // Set the enemy's state to ATTACKING and reset its state time
        attackingEnemy.setState(EnemyState.ATTACKING);
        enemyStateTime = 0f;

        // Apply damage to the player
        player.setHealth(player.getHealth() - 15);
        player.setState(PlayerState.HURT);

        // Check if the player is defeated
        if (player.getHealth() <= 0) {
            player.setState(PlayerState.DEAD);
            isGameOver = true;
            playerWon = false;
        }

        // After attack, return enemy to IDLE and hand over turn to player
        attackingEnemy.setState(EnemyState.IDLE);
        playerTurn = true;
        turnDelayTimer = 0f;
    }


    private TextureRegion getPlayerFrame() {
        switch (player.getState()) {
            case ATTACKING:
                if (player.getAttackAnimation().isAnimationFinished(playerStateTime) && actionDelayTimer > 0.5f) {
                    player.setState(PlayerState.IDLE);
                    playerTurn = false;
                    turnDelayTimer = 0f;
                }
                return player.getAttackAnimation().getKeyFrame(playerStateTime, false);
            case HURT:
                if (player.getHurtAnimation().isAnimationFinished(playerStateTime)) {
                    actionDelayTimer = 0f;
                    player.setState(PlayerState.IDLE);
                }
                return player.getHurtAnimation().getKeyFrame(playerStateTime, false);
            case DEAD:
                return player.getDeathAnimation().getKeyFrame(playerStateTime, false);
            case IDLE:
            default:
                return player.getIdleAnimation().getKeyFrame(playerStateTime, true);
        }
    }

    private TextureRegion getEnemyFrame(Enemy enemy) {
        switch (enemy.getState()) {
            case ATTACKING:
                if (enemy.getAttackAnimation().isAnimationFinished(enemyStateTime)) {
                    enemy.setState(EnemyState.IDLE);
                }
                return enemy.getAttackAnimation().getKeyFrame(enemyStateTime, false);
            case HURT:
                if (enemy.getHurtAnimation().isAnimationFinished(enemyStateTime) && actionDelayTimer > 0.5f) {
                    enemy.setState(EnemyState.IDLE);
                }
                return enemy.getHurtAnimation().getKeyFrame(enemyStateTime, false);
            case DEAD:
                if (enemy.getDeathAnimation().isAnimationFinished(enemyStateTime) && actionDelayTimer > 0.5f) {
                    removeDefeatedEnemy(enemy);
                }// Remove enemy after delay
            case IDLE:
            default:
                return enemy.getIdleAnimation().getKeyFrame(enemyStateTime, true);
        }
    }

    private void removeDefeatedEnemy(Enemy enemy) {
        int index = enemies.indexOf(enemy);
        if (index >= 0) { // Ensure the index is valid
            System.out.println("Removing defeated enemy...");
            enemies.remove(index);
            if (index < enemyHealthBars.size()) {
                HealthBar healthBar = enemyHealthBars.get(index);
                healthBar.removeFromStage(); // Remove the health bar from the stage
                enemyHealthBars.remove(index); // Remove the health bar from the list
            }
            if (enemies.isEmpty()) {
                isCombatOver = true;
                playerWon = true;
                endCombat();
            }
        }
    }

    private void endCombat() {
        Vector2 lastPlayerPosition = parent.getGameScreen().getPlayerPosition(); // Get current player position before leaving GameScreen
        if (isCombatOver) {
            if (playerWon) {
                System.out.println("Player won the combat!");
                parent.getGameScreen().restorePlayerPosition(lastPlayerPosition);
                parent.getGameScreen().returnToOverworldWithWin();
            } else {
                System.out.println("Player lost the combat!");
                parent.getGameScreen().returnToOverworldWithLoss();
            }
        }
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
    public void resize(int width, int height) {

        stage.getViewport().update(width, height, true);
        // Reposition health bars to fixed positions on the screen
        playerHealthBar.setPosition(50, height - playerHealthBar.getHeight() - 50);
        for (HealthBar enemyHealthBar : enemyHealthBars) {
            enemyHealthBar.setPosition(width - enemyHealthBar.getWidth() - 100, height - enemyHealthBar.getHeight() - 50);
        }

        // Lock player and enemy positions to the screen
        // Ensure their coordinates are based on a fixed reference rather than adjusting dynamically with the viewport
        player.setPosition(100, 150);  // Fixed position for player
        enemies.get(0).setPosition(width - 300, 150);  // Fixed position for enemy
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void dispose() {
        batch.dispose();
        combatBackground.dispose();
        stage.dispose();
        skin.dispose();
    }
}
