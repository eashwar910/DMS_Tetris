// used ai to create a dynamic start screen using java fx
// prompt :
//i want to create a dynamic start screen for the game.
//I want multiple layers to it. the furthest layer at the back would be
// a grid that is pulsating. and the layer in front of it will have neon tetrominoes
// emerging from outside the screen on the left and dashing towards the right side of
// the green at random speeds and at random y acis spawn points. it should not be too fast
// and it should not be too slow. each tetromino should have a random speed. and add a
// speed range to each of them. when two tetrominoes collide, it should burst into small
// particles. the front most layer would be text and the buttons as is right now

package com.comp2042.ui;

import com.comp2042.logic.bricks.*;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.animation.Animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Animated start screen with pulsating grid, flying tetrominoes, and particles.
 * Manages animations and rendering across layered canvases.
 *
 * @author Eashwar
 * @version 1.0
 */
public class DynamicStartScreen extends Pane {

    private final Canvas backgroundCanvas, tetrominoCanvas, particleCanvas;
    private final List<FlyingTetromino> tetrominoes = new ArrayList<>();
    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();
    private final BrickGenerator brickGenerator = new RandomBrickGenerator();
    private AnimationTimer animationTimer;
    private Timeline spawnTimer;
    private double pulsePhase = 0;

    private static final int GRID_SIZE = 30, BLOCK_SIZE = 15;
    private static final double PULSE_MIN = 0.08, PULSE_MAX = 0.18, SPAWN_INTERVAL = 0.5;
    private static final Color[] COLORS = {null, Color.rgb(0,180,180), Color.rgb(0,0,180),
            Color.rgb(200,120,0), Color.rgb(200,200,0), Color.rgb(0,180,0),
            Color.rgb(100,0,180), Color.rgb(180,0,0)};

    /**
     * Constructs the start screen and initializes canvases and animations.
     */
    public DynamicStartScreen() {
        backgroundCanvas = new Canvas();
        tetrominoCanvas = new Canvas();
        particleCanvas = new Canvas();
        getChildren().addAll(backgroundCanvas, tetrominoCanvas, particleCanvas);

        setMouseTransparent(false);
        backgroundCanvas.setMouseTransparent(true);
        tetrominoCanvas.setMouseTransparent(true);
        particleCanvas.setMouseTransparent(true);

        setupAnimations();
    }

    private void setupAnimations() {
        animationTimer = new AnimationTimer() {
            private long lastUpdate = 0;
            @Override
            public void handle(long now) {
                if (lastUpdate == 0) { lastUpdate = now; return; }
                double dt = (now - lastUpdate) / 1e9;
                lastUpdate = now;
                update(dt);
                render();
            }
        };

        spawnTimer = new Timeline(new KeyFrame(Duration.seconds(SPAWN_INTERVAL),
                e -> spawnTetromino()));
        spawnTimer.setCycleCount(Animation.INDEFINITE);
    }

    private void spawnTetromino() {
        if (getWidth() > 0 && getHeight() > 0) {
            tetrominoes.add(new FlyingTetromino(-100, random.nextDouble() * getHeight(),
                    80 + random.nextDouble() * 120, brickGenerator.getBrick()));
        }
    }

    private void update(double dt) {
        pulsePhase += dt * 1.5;

        tetrominoes.removeIf(t -> { t.update(dt); return t.x > getWidth() + 100; });
        particles.removeIf(p -> { p.update(dt); return p.isDead(); });

        checkCollisions();
    }

    private void checkCollisions() {
        List<FlyingTetromino> toRemove = new ArrayList<>();
        for (int i = 0; i < tetrominoes.size(); i++) {
            FlyingTetromino t1 = tetrominoes.get(i);
            for (int j = i + 1; j < tetrominoes.size(); j++) {
                FlyingTetromino t2 = tetrominoes.get(j);
                if (t1.collidesWith(t2)) {
                    createBurst((t1.x + t2.x) / 2, (t1.y + t2.y) / 2, t1.color, t2.color);
                    toRemove.add(t1);
                    toRemove.add(t2);
                    break;
                }
            }
        }
        tetrominoes.removeAll(toRemove);
    }

    private void createBurst(double x, double y, Color c1, Color c2) {
        for (int i = 0; i < 15 + random.nextInt(10); i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 50 + random.nextDouble() * 100;
            particles.add(new Particle(x, y, Math.cos(angle) * speed,
                    Math.sin(angle) * speed, random.nextBoolean() ? c1 : c2));
        }
    }

    private void render() {
        renderBackground();
        renderTetrominoes();
        renderParticles();
    }

    private void renderBackground() {
        GraphicsContext gc = backgroundCanvas.getGraphicsContext2D();
        double w = getWidth(), h = getHeight();
        gc.clearRect(0, 0, w, h);

        double opacity = PULSE_MIN + (PULSE_MAX - PULSE_MIN) * (0.5 + 0.5 * Math.sin(pulsePhase));
        gc.setStroke(Color.rgb(255, 255, 255, opacity));
        gc.setLineWidth(1);

        for (double x = 0; x < w; x += GRID_SIZE) gc.strokeLine(x, 0, x, h);
        for (double y = 0; y < h; y += GRID_SIZE) gc.strokeLine(0, y, w, y);
    }

    private void renderTetrominoes() {
        GraphicsContext gc = tetrominoCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        tetrominoes.forEach(t -> t.render(gc));
    }

    private void renderParticles() {
        GraphicsContext gc = particleCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        particles.forEach(p -> p.render(gc));
    }

    /**
     * Starts background and spawn animations.
     */
    public void start() { animationTimer.start(); spawnTimer.play(); }
    /**
     * Stops animations and clears active entities.
     */
    public void stop() { animationTimer.stop(); spawnTimer.stop(); tetrominoes.clear(); particles.clear(); }

    @Override
    /**
     * Resizes layered canvases to fill the component.
     */
    protected void layoutChildren() {
        super.layoutChildren();
        double w = getWidth(), h = getHeight();
        backgroundCanvas.setWidth(w); backgroundCanvas.setHeight(h);
        tetrominoCanvas.setWidth(w); tetrominoCanvas.setHeight(h);
        particleCanvas.setWidth(w); particleCanvas.setHeight(h);
    }

    private class FlyingTetromino {
        double x, y, speed, rotation, scale;
        int[][] shape;
        Color color;

        FlyingTetromino(double x, double y, double speed, Brick brick) {
            this.x = x; this.y = y; this.speed = speed;
            this.shape = brick.getShapeMatrix().get(0);
            this.color = extractColor(shape);
            this.rotation = random.nextDouble() * 360;
            this.scale = 0.8 + random.nextDouble() * 0.6;
        }

        Color extractColor(int[][] shape) {
            for (int[] row : shape)
                for (int val : row)
                    if (val > 0 && val < COLORS.length) return COLORS[val];
            return COLORS[1];
        }

        void update(double dt) { x += speed * dt; }

        void render(GraphicsContext gc) {
            gc.save();
            gc.translate(x, y);
            gc.rotate(rotation);
            gc.scale(scale, scale);

            int rows = shape.length, cols = shape[0].length;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (shape[r][c] > 0) {
                        double bx = (c - cols / 2.0) * BLOCK_SIZE;
                        double by = (r - rows / 2.0) * BLOCK_SIZE;

                        gc.setFill(Color.rgb(0, 0, 0, 0.5));
                        gc.fillRect(bx, by, BLOCK_SIZE - 2, BLOCK_SIZE - 2);

                        DropShadow glow = new DropShadow(BlurType.GAUSSIAN, color, 6, 0.5, 0, 0);
                        gc.setEffect(glow);
                        gc.setStroke(color);
                        gc.setLineWidth(2);
                        gc.strokeRect(bx, by, BLOCK_SIZE - 2, BLOCK_SIZE - 2);
                        gc.setEffect(null);
                    }
                }
            }
            gc.restore();
        }

        boolean collidesWith(FlyingTetromino other) {
            double size = Math.max(shape.length, shape[0].length) * BLOCK_SIZE * scale;
            double otherSize = Math.max(other.shape.length, other.shape[0].length) * BLOCK_SIZE * other.scale;
            double dx = x - other.x, dy = y - other.y;
            return Math.sqrt(dx * dx + dy * dy) < (size + otherSize) / 2;
        }
    }

    private static class Particle {
        double x, y, vx, vy, life = 1.5;
        Color color;

        Particle(double x, double y, double vx, double vy, Color color) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy; this.color = color;
        }

        void update(double dt) {
            x += vx * dt; y += vy * dt;
            vy += 200 * dt;
            vx *= 0.98;
            life -= dt;
        }

        boolean isDead() { return life <= 0; }

        void render(GraphicsContext gc) {
            double alpha = Math.max(0, life / 1.5);
            double size = 4 + (1 - alpha) * 2;
            gc.setFill(Color.rgb((int)(color.getRed() * 255),
                    (int)(color.getGreen() * 255), (int)(color.getBlue() * 255), alpha));
            gc.fillOval(x - size / 2, y - size / 2, size, size);
        }
    }
}
