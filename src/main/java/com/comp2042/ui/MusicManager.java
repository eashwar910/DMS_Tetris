package com.comp2042.ui;

import java.io.File;
import java.net.URL;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public final class MusicManager {

    private MediaPlayer gamePlayer;
    private MediaPlayer startPlayer;
    private MediaPlayer fxPlayer;
    private AudioClip clickClip;
    private final String gamePath;

    public MusicManager(String gameFilePath) {
        this.gamePath = gameFilePath;
        gamePlayer = createLoopingPlayer(gameFilePath);
    }

    // media player function
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
    private MediaPlayer createLoopingPlayer(String path) { return createPlayer(path, true); }
    private MediaPlayer createOneShotPlayer(String path) { return createPlayer(path, false); }

    public void playLoopFromStart() {
        if (gamePlayer == null) gamePlayer = createLoopingPlayer(gamePath);
        if (gamePlayer == null) return;
        gamePlayer.stop();
        gamePlayer.play();
    }

    // wrappers for easy use
    public void pause() { if (gamePlayer != null) gamePlayer.pause(); }
    public void resume() { if (gamePlayer != null) gamePlayer.play(); }
    public void stop() { if (gamePlayer != null) gamePlayer.stop(); }
    public void restart() { playLoopFromStart(); }

    public void startStartLoop(String path) {
        stopStart();
        startPlayer = createLoopingPlayer(path);
        if (startPlayer != null) startPlayer.play();
    }

    public void stopStart() { if (startPlayer != null) { startPlayer.stop(); startPlayer = null; } }

    public void playOnce(String path) {
        stopFx();
        fxPlayer = createOneShotPlayer(path);
        if (fxPlayer != null) fxPlayer.play();
    }

    public void stopFx() { if (fxPlayer != null) { fxPlayer.stop(); fxPlayer = null; } }

    // method to play sound for click
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
