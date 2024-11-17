package io.github.louisnight.turnbasedrpg.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
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

public class CombatScreen implements Screen {

    private SpriteBatch batch;
    private Player player;
    private Enemy enemy;
    private Texture combatBackground;

    private boolean playerTurn;
    private boolean isGameOver;
    private boolean playerWon;

    private float stateTime;
    private float actionDelayTimer = 0f;
    private float turnDelayTimer = 0f;
    private final float ACTION_DELAY = 1.5f; // Delay between actions
    private final float TURN_DELAY = 2f;    // Additional delay between turns

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
        attackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (playerTurn && !isGameOver && turnDelayTimer >= TURN_DELAY) {
                    playerAttack();
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

        // Layout the buttons using a table
        Table table = new Table();
        table.bottom().pad(10);
        table.setFillParent(true);
        table.add(attackButton).padRight(10);
        table.add(defendButton).padLeft(10);

        // Add the table to the stage
        stage.addActor(table);

        // Set input processor to the stage
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        stateTime += delta;
        actionDelayTimer += delta;
        turnDelayTimer += delta;

        batch.begin();
        batch.draw(combatBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        TextureRegion playerFrame = getPlayerFrame();
        TextureRegion enemyFrame = getEnemyFrame();

        batch.draw(playerFrame, 160, 180, 128, 128);
        batch.draw(enemyFrame, 600, 180, 128, 128);
        batch.end();

        stage.act(delta);
        stage.draw();

        if (!isGameOver) {
            if (!playerTurn && actionDelayTimer >= ACTION_DELAY && turnDelayTimer >= TURN_DELAY) {
                enemyTurn();
            }
        } else {
            endCombat();
        }
    }

    private TextureRegion getPlayerFrame() {
        switch (player.getState()) {
            case ATTACKING:
                if (player.getAttackAnimation().isAnimationFinished(stateTime)) {
                    player.setState(PlayerState.IDLE);
                }
                return player.getAttackAnimation().getKeyFrame(stateTime, false);
            case HURT:
                if (player.getHurtAnimation().isAnimationFinished(stateTime)) {
                    player.setState(PlayerState.IDLE);
                }
                return player.getHurtAnimation().getKeyFrame(stateTime, false);
            case DEAD:
                return player.getDeathAnimation().getKeyFrame(stateTime, false);
            case IDLE:
            default:
                return player.getIdleAnimation().getKeyFrame(stateTime, true);
        }
    }

    private TextureRegion getEnemyFrame() {
        switch (enemy.getState()) {
            case ATTACKING:
                if (enemy.getAttackAnimation().isAnimationFinished(stateTime)) {
                    enemy.setState(EnemyState.IDLE);
                }
                return enemy.getAttackAnimation().getKeyFrame(stateTime, false);
            case HURT:
                if (enemy.getHurtAnimation().isAnimationFinished(stateTime)) {
                    enemy.setState(EnemyState.IDLE);
                }
                return enemy.getHurtAnimation().getKeyFrame(stateTime, false);
            case DEAD:
                return enemy.getDeathAnimation().getKeyFrame(stateTime, false);
            case IDLE:
            default:
                return enemy.getIdleAnimation().getKeyFrame(stateTime, true);
        }
    }

    public void playerAttack() {
        System.out.println("Player attacks!");
        player.setState(PlayerState.ATTACKING);
        actionDelayTimer = 0f;
        turnDelayTimer = 0f;
        stateTime = 0f;

        enemy.setHealth(enemy.getHealth() - 20f);
        enemy.setState(EnemyState.HURT);

        if (enemy.getHealth() <= 0) {
            enemy.setState(EnemyState.DEAD);
            isGameOver = true;
            playerWon = true;
            System.out.println("Enemy defeated!");
        } else {
            playerTurn = false; // End player's turn
        }
    }

    private void playerDefend() {
        System.out.println("Player defends!");
        turnDelayTimer = 0f;
        playerTurn = false; // End player's turn
    }

    private void enemyTurn() {
        System.out.println("Enemy's Turn");

        enemyAttack();
    }

    private void enemyAttack() {
        System.out.println("Enemy attacks!");
        enemy.setState(EnemyState.ATTACKING);
        actionDelayTimer = 0f;
        turnDelayTimer = 0f;
        stateTime = 0f;

        player.setHealth(player.getHealth() - 15);
        player.setState(PlayerState.HURT);

        if (player.getHealth() <= 0) {
            player.setState(PlayerState.DEAD);
            isGameOver = true;
            playerWon = false;
            System.out.println("Player defeated!");
        } else {
            enemy.setState(EnemyState.IDLE);
            playerTurn = true; // Hand over turn to player
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
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

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
