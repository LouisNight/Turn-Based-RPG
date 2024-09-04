package io.github.louisnight.turnbasedrpg;

import com.badlogic.gdx.Game;
import io.github.louisnight.turnbasedrpg.views.*;


    // private Texture knightImage;
    //    private Texture bucketImage;
//    private Sound dropSound;
    // private Music townMusic;




/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

        public class TestRPG extends Game {

    private LoadingScreen loadingScreen;
    private PreferencesScreen preferencesScreen;
    private MainScreen mainScreen;
    private EndScreen endScreen;
    private MenuScreen menuScreen;

    public final static int MENU = 0;
    public final static int PREFERENCES = 1;
    public final static int APPLICATION = 2;
    public final static int ENDGAME = 3;

    @Override
    public void create() {
        loadingScreen = new LoadingScreen(this);
        setScreen(loadingScreen);

        // load the images for the droplet and the bucket, 64x64 pixels each
        //  dropImage = new Texture(Gdx.files.internal("droplet.png"));
        // bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        // load the drop sound effect and the rain background "music"
        // dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        // rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        // start the playback of the background music immediately
        //  rainMusic.setLooping(true);
        // rainMusic.play();
    }

    public void changeScreen(int screen) {
        switch (screen) {
            case MENU:
                if (menuScreen == null) menuScreen = new MenuScreen(this);
                this.setScreen(menuScreen);
                break;
            case PREFERENCES:
                if (preferencesScreen == null) preferencesScreen = new PreferencesScreen(this);
                this.setScreen(preferencesScreen);
                break;
            case APPLICATION:
                if (mainScreen == null) mainScreen = new MainScreen(this);
                this.setScreen(mainScreen);
                break;
            case ENDGAME:
                if (endScreen == null) endScreen = new EndScreen(this);
                this.setScreen(endScreen);
                break;
        }
    }

}