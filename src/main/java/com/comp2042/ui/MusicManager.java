package com.comp2042.ui;

import java.io.File;
import java.net.URL;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Manages game audio playback for music loops and effects, including
 * start-screen music, gameplay loop, one-shot FX, and click sounds.
 *
 * @author Eashwar
 * @version 1.0
 */
public final class MusicManager {

    private MediaPlayer gamePlayer;
    private MediaPlayer startPlayer;
    private MediaPlayer fxPlayer;
    private AudioClip clickClip;
    private final String gamePath;

    /**
     * Constructs a music manager with the main game music path.
     *
     * @param gameFilePath path to the game loop music
     */
    public MusicManager(String gameFilePath) {
        this.gamePath = gameFilePath;
        gamePlayer = createLoopingPlayer(gameFilePath);
    }

    // media player function
    /**
     * Creates a media player for the given path.
     * Tries classpath first, then filesystem.
     *
     * @param path audio path
     * @param loop whether to loop
     * @return media player or null if unavailable
     */
    private MediaPlayer createPlayer(String path, boolean loop) {
        Media media = null;
        URL url = getClass().getClassLoader().getResource(path);
        if (url != null)
        {
            media = new Media(url.toExternalForm());
        }
        else
        {
            File f = new File(path);
            if (f.exists()) media = new Media(f.toURI().toString());
        }
        if (media == null) return null;
        MediaPlayer mp = new MediaPlayer(media);
        mp.setCycleCount(loop ? MediaPlayer.INDEFINITE : 1);
        return mp;
    }

    // different playing methods
    /**
     * Creates a looping media player.
     *
     * @param path audio path
     * @return media player or null
     */
    private MediaPlayer createLoopingPlayer(String path) { return createPlayer(path, true); }
    /**
     * Creates a one-shot media player.
     *
     * @param path audio path
     * @return media player or null
     */
    private MediaPlayer createOneShotPlayer(String path) { return createPlayer(path, false); }

    /**
     * Starts the game loop music from the beginning.
     */
    public void playLoopFromStart() {
        if (gamePlayer == null) gamePlayer = createLoopingPlayer(gamePath);
        if (gamePlayer == null) return;
        gamePlayer.stop();
        gamePlayer.play();
    }

    // wrappers for easy use
    /**
     * Pauses the game loop music.
     */
    public void pause() { if (gamePlayer != null) gamePlayer.pause(); }
    /**
     * Resumes the game loop music.
     */
    public void resume() { if (gamePlayer != null) gamePlayer.play(); }
    /**
     * Stops the game loop music.
     */
    public void stop() { if (gamePlayer != null) gamePlayer.stop(); }
    /**
     * Restarts the game loop music from the beginning.
     */
    public void restart() { playLoopFromStart(); }

    /**
     * Starts looping start-screen music.
     *
     * @param path audio path
     */
    public void startStartLoop(String path) {
        stopStart();
        startPlayer = createLoopingPlayer(path);
        if (startPlayer != null) startPlayer.play();
    }

    /**
     * Stops the start-screen music.
     */
    public void stopStart() { if (startPlayer != null) { startPlayer.stop(); startPlayer = null; } }

    /**
     * Plays a one-shot sound effect.
     *
     * @param path audio path
     */
    public void playOnce(String path) {
        stopFx();
        fxPlayer = createOneShotPlayer(path);
        if (fxPlayer != null) fxPlayer.play();
    }

    /**
     * Stops any active one-shot sound effect.
     */
    public void stopFx() { if (fxPlayer != null) { fxPlayer.stop(); fxPlayer = null; } }

    // method to play sound for click
    /**
     * Plays the UI click sound.
     */
    public void playClick() {
        if (clickClip == null) {
            URL url = getClass().getClassLoader().getResource("sound/click.wav");
            if (url != null) {
                clickClip = new AudioClip(url.toExternalForm());
            } else {
                File f = new File("sound/click.wav");
                if (f.exists()) clickClip = new AudioClip(f.toURI().toString());
            }
        }
        if (clickClip != null) clickClip.play();
    }
}
