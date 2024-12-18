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
import com.badlogic.gdx.utils.Timer;
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
    private float ACTION_DELAY = 1.5f; // Delay between actions
    private final float TURN_DELAY = 2f;    // Additional delay between turns

    private HealthBar playerHealthBar;
    private List<HealthBar> enemyHealthBars = new ArrayList<>();
    private Stage stage;
    private Skin skin;
    private TextButton attackButton;
    private TextButton defendButton;
    private boolean isCombatOver;

    private final float PLAYER_SCALE = 1.5f;
    private final float ENEMY_SCALE = 1.5f;

    private TestRPG parent;

    public CombatScreen(TestRPG parent, Player player, List<Enemy> enemies, HealthBar playerHealthBar) {
        this.parent = parent;
        this.player = player;
        this.enemies = enemies;
        this.isCombatOver = false;


        this.playerHealthBar = playerHealthBar;
        this.playerHealthBar.setSize(150,30);
        playerHealthBar.setPosition(50, Gdx.graphics.getHeight() - playerHealthBar.getHeight() - 50);


        for (Enemy enemy : enemies) {
            HealthBar enemyHealthBar = new HealthBar(
                new Image(new Texture("../assets/UI/Health_01_16x16.png")),
                new Image(new Texture("../assets/UI/Health_01_Bar01_16x16.png"))
            );
            enemyHealthBar.setSize(150,30);
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

        float playerX = 100; // Adjust for positioning
        float playerY = 150;
        TextureRegion playerFrame = getPlayerFrame();
        batch.draw(
            playerFrame,
            playerX, playerY,
            playerFrame.getRegionWidth() * PLAYER_SCALE,  // Scaled width
            playerFrame.getRegionHeight() * PLAYER_SCALE // Scaled height
        );

        if (!enemies.isEmpty()) {
            float enemyX = Gdx.graphics.getWidth() - 300; // Adjust for positioning
            float enemyY = 150;
            TextureRegion enemyFrame = getEnemyFrame(enemies.get(0));
            batch.draw(
                enemyFrame,
                enemyX, enemyY,
                enemyFrame.getRegionWidth() * ENEMY_SCALE,  // Scaled width
                enemyFrame.getRegionHeight() * ENEMY_SCALE // Scaled height
            );
        }

        batch.end();

        stage.act(delta);
        stage.draw();

        if (!isGameOver) {
            if (playerTurn) {
                if (player.getState() == PlayerState.IDLE && turnDelayTimer >= TURN_DELAY) {
                    stage.act(delta);
                    stage.draw();
                }
            } else if (!enemies.isEmpty()) {
                Enemy attackingEnemy = enemies.get(0);
                if (attackingEnemy.getState() == EnemyState.ATTACKING) {
                    if (attackingEnemy.getAttackAnimation().isAnimationFinished(enemyStateTime)) {
                        System.out.println("Enemy attack animation finished.");
                        attackingEnemy.setState(EnemyState.IDLE);
                        playerTurn = true;
                        turnDelayTimer = 0f;
                    }
                } else if (actionDelayTimer >= ACTION_DELAY) {
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
        targetEnemy.setHealth(targetEnemy.getCurrentHealth() - 25f);
        targetEnemy.setState(EnemyState.HURT);

        if (targetEnemy.getCurrentHealth() <= 0) {
            targetEnemy.setState(EnemyState.DEAD);
            actionDelayTimer = 0f;
            System.out.println("Enemy defeated!");
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
        if (enemies.isEmpty()) {
            System.out.println("No enemies remaining. Ending combat.");
            isGameOver = true;
            playerWon = true;
            return;
        }

        Enemy attackingEnemy = enemies.get(0);
        if (attackingEnemy == null || attackingEnemy.getCurrentHealth() <= 0) {
            System.out.println("Enemy is invalid or defeated. Skipping turn.");
            isCombatOver = true;
            playerWon = true;
            endCombat(); // Transition to game world
            return;
        }

        System.out.println("Enemy starts attacking!");
        enemyAttack(attackingEnemy);
    }


    private void enemyAttack(Enemy attackingEnemy) {
        System.out.println("Enemy attacks!");
        attackingEnemy.setState(EnemyState.ATTACKING); // Set state to ATTACKING
        enemyStateTime = 0f; // Reset state time for the animation

        float animationDuration = attackingEnemy.getAttackAnimation().getAnimationDuration();
        actionDelayTimer = Math.max(animationDuration + 0.3f, ACTION_DELAY);

        // Schedule damage application after animation starts
        Gdx.app.postRunnable(() -> {
            player.setHealth(player.getHealth() - 15);
            player.setState(PlayerState.HURT);

            System.out.println("Player health after attack: " + player.getHealth());

            // Check if the player is defeated
            if (player.getHealth() <= 0) {
                System.out.println("Player defeated!");
                player.setState(PlayerState.DEAD);
                isGameOver = true;
                playerWon = false;
            }
        });

        // Schedule transition to IDLE after the animation completes
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                System.out.println("Enemy attack animation finished. Switching to IDLE.");
                attackingEnemy.setState(EnemyState.IDLE);
                playerTurn = true;
                turnDelayTimer = 0f;
            }
        }, animationDuration);
    }
    private boolean isBossFight() {
        return !enemies.isEmpty() && enemies.get(0).getClass().getSimpleName().equals("DungeonBoss");
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
                return enemy.getAttackAnimation().getKeyFrame(enemyStateTime, false);

            case IDLE:
                return enemy.getIdleAnimation().getKeyFrame(enemyStateTime, true);

            case DEAD:
                return enemy.getDeathAnimation().getKeyFrame(enemyStateTime, false);

            default:
                return enemy.getIdleAnimation().getKeyFrame(enemyStateTime, true);
        }
    }


    private void endCombat() {
        Vector2 lastPlayerPosition = parent.getGameScreen().getPlayerPosition(); // Get current player position

        // Check if the player is dead
        if (player.getHealth() <= 0) {
            System.out.println("Player's health is zero. Triggering loss scenario.");
            isCombatOver = true;
            playerWon = false;
        }

        if (isCombatOver) {
            if (playerWon) {
                System.out.println("Player won the combat!");
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        parent.getGameScreen().restorePlayerPosition(lastPlayerPosition);
                        parent.getGameScreen().returnToOverworldWithWin();
                    }
                }, 1f); // Delay for 1 second to allow any final animations or effects
            } else {
                System.out.println("Player lost the combat!");
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        if (isBossFight()) {
                            System.out.println("Player lost to the boss. Transitioning to a new map.");
                            parent.getGameScreen().transitionToNewMap("../assets/Maps/OverworldMap.tmx", new Vector2(400, 650)); // Replace with actual map path and spawn point
                        } else {
                            parent.getGameScreen().returnToOverworldWithLoss();
                        }
                    }
                }, 1f);
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
