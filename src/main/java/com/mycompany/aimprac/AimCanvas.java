/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aimprac;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jokin
 */
public class AimCanvas {
    
    private static final Logger LOG = LoggerFactory.getLogger(AimCanvas.class);
    
    private double canvasWidth;
    private double canvasHeight;
    private final double canvasSafeZone = 80;

    private final AnchorPane rootPane;
    private double rootWidth;
    private double rootHeight;
    private Canvas canvas;
    private GraphicsContext gc;
    private boolean started = false;
    private boolean playSounds;
    
    
    private Random rand;
    private ArrayList<Dot> dots;
    
    private String mode = "close_dots";
    private double dotRadius;
    private long dotVisibilityMillis;
    private int multiDotCount;
    private long addDotDelayMillis;
    private int addDots;

    private double lastHitX = -1;
    private double lastHitY = -1;
    private boolean lastHitHitted;
    private int missedShotCount = 0;

    private AimCanvasTimer timer;

    public AimCanvas(double canvasWidth, double canvasHeight, String mode, double dotRadius, int addDots, long dotVisibilityMillis, int multiDotCount, long addDotDelayMillis, boolean playSounds) {
        this.rootPane = new AnchorPane();
        this.mode = mode;
        this.dotRadius = dotRadius;
        this.addDots = addDots;
        this.dotVisibilityMillis = dotVisibilityMillis;
        this.multiDotCount = multiDotCount;
        this.addDotDelayMillis = addDotDelayMillis;
        this.playSounds = playSounds;
        this.setWidth(canvasWidth);
        this.setHeight(canvasHeight);
    }

    public void clear() {
        this.gc.setFill(Color.rgb(60, 60, 70));
        this.gc.fillRect(0, 0, this.canvasWidth, this.canvasHeight);
    }

    public AnchorPane getView() {
        return this.rootPane;
    }

    public GraphicsContext getGc() {
        return gc;
    }

    public void setCursor(Cursor cursor) {
        this.canvas.setCursor(cursor);
    }
    
    private int activeDotCount() {
        int activeDots = 0;
        for (Dot dot : dots) {
            if (dot.isActive()) activeDots++;
        }
        return activeDots;
    }
    
    private int hittedDotCount() {
        int hittedDots = 0;
        for (Dot dot : dots) {
            if (dot.isHitted()) hittedDots++;
        }
        return hittedDots;
    }
    
    private int missedDotCount() {
        int missedDots = 0;
        for (Dot dot : dots) {
            if (dot.isTimeouted() && !dot.isHitted()) missedDots++;
        }
        return missedDots;
    }

    public void addNewDots() {
        for (int i = 0; i < this.multiDotCount - activeDotCount(); i++) {
            if (this.multiDotCount > 1) {
                if (System.currentTimeMillis() - this.dots.get(this.dots.size()-1).getStartMillis() >= this.addDotDelayMillis) {
                    addNewDot();
                }
            } else {
                if (System.currentTimeMillis() - getInactiveMillis() >= this.addDotDelayMillis) {
                    addNewDot();
                }
            }
        }
    }
    
    private void addNewDot() {
        switch (this.mode) {
            case "close_dots":
                this.addCloseDot();
                return;
            default:
                this.addRandomDot();
        }
    }
    
    private long getInactiveMillis() {
        for (int i = this.dots.size() - 1; i >= 0; i--) {
            Dot dot = this.dots.get(i);
            if (dot.isHitted()) {
                return dot.getHittedMillis();
            }
            if (dot.isTimeouted()) {
                return dot.getTimeoutMillis();
            }
        }
        return 0;
    }

    public void drawDots() {
        for (int i = this.dots.size() - 1; i >= 0; i--) {
            Dot dot = this.dots.get(i);
            dot.draw();
        }
    }
    
    private void drawResultsToCanvas() {
        this.clear();
        for (int i = 0; i + 1 < dots.size(); i++) {
            Dot dot1 = this.dots.get(i);
            Dot dot2 = this.dots.get(i + 1);
            gc.setStroke(Color.rgb(128, 30, 215, 0.4));
            gc.strokeLine(dot1.getX(), dot1.getY(), dot2.getX(), dot2.getY());
        }
        for (Dot dot : dots) {
            dot.drawResult();
        }
    }

    public void drawLastHit() {
        if (this.lastHitX >= 0 && this.lastHitY >= 0) {
            this.gc.setFill(this.lastHitHitted ? Color.GREEN : Color.RED);
            this.gc.fillOval(this.lastHitX - 4, this.lastHitY - 4, 8, 8);
        }
    }

    public void drawPerformanceInfo() {
        double fontSize = Math.min(this.canvasWidth, this.canvasHeight)/10;
        gc.setFill(Color.rgb(50, 50, 60, 0.5));
        gc.setFont(Font.font(fontSize));
        gc.fillText("" + (this.addDots - this.hittedDotCount() - this.missedDotCount()), this.canvasWidth/2 - fontSize/2, this.canvasHeight/2);
    }

    public void drawStartInfo() {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(18));
        gc.fillText("Start by clicking this.", this.canvasWidth / 2 - 70, this.canvasHeight / 2 - 40);
    }

    public void addRandomDot() {
        if (this.dots.size() >= this.addDots) {
            return;
        }
        double newX = this.rand.nextInt((int) (this.canvasWidth - this.dotRadius * 2)) + this.dotRadius;
        double newY = this.rand.nextInt((int) (this.canvasHeight - this.dotRadius * 2)) + this.dotRadius;
        addDot(newX, newY);
    }
    
    public void addCloseDot() {
        if (this.dots.size() >= this.addDots) {
            return;
        }
        double frameWidth = this.dotRadius * 16;
        double frameHeight = frameWidth * this.canvasHeight / this.canvasWidth;
        double drawAreaWidth = this.canvasWidth - this.dotRadius*2;
        double drawAreaHeight = this.canvasHeight - this.dotRadius*2;
        
        Dot lastDot = this.dots.get(this.dots.size()-1);
        double closeFrameOffsetX = (lastDot.getX() - this.canvasWidth/2) / (drawAreaWidth / 2);
        double closeFrameOffsetY = (lastDot.getY() - this.canvasHeight/2) / (drawAreaHeight / 2);
        closeFrameOffsetX = -frameWidth/2 - frameWidth/2 * Math.pow(closeFrameOffsetX, 3);
        closeFrameOffsetY = -frameHeight/2 - frameHeight/2 * Math.pow(closeFrameOffsetY, 3);
        
        double newX = lastDot.getX() + closeFrameOffsetX + this.rand.nextInt((int) frameWidth);
        double newY = lastDot.getY() + closeFrameOffsetY + this.rand.nextInt((int) frameHeight);
        
        addDot(newX, newY);
        
//        this.gc.setStroke(Color.RED);
//        this.gc.strokeRect(
//                newX + closeFrameOffsetX, 
//                newY + closeFrameOffsetY, 
//                frameWidth, 
//                frameHeight
//        );
    }
    
    private void addDot(double x, double y) {
        x = Math.min(Math.max(x, this.dotRadius), this.canvasWidth - this.dotRadius);
        y = Math.min(Math.max(y, this.dotRadius), this.canvasHeight - this.dotRadius);
        this.dots.add(new Dot(x, y, this.dotRadius, this.dotVisibilityMillis, this.gc));
    }

    public void hit(double x, double y) {
        for (Dot dot : dots) {
            if (dot.hit(x, y)) {
                playSound("switch-17.wav");
                this.lastHitHitted = true;
                break;
            }
            this.lastHitHitted = false;
        }
        if (!lastHitHitted) {
            playSound("beep-08b.wav");
            this.missedShotCount++;
        }
        this.lastHitX = x;
        this.lastHitY = y;
    }

    public void update() {
        this.clear();
        this.drawPerformanceInfo();
        if (!this.started) {
            drawStartInfo();
            return;
        }
        if (activeDotCount() == 0 && this.dots.size() >= this.addDots) {
            this.stop();
            this.showResults();
            return;
        }
        this.addNewDots();
        this.drawDots();
        this.drawLastHit();
    }

    public void start() {
        this.canvas.setOnMousePressed((event) -> {
            if (!this.started) {
                this.dots.add(new Dot(this.canvasWidth/2, this.canvasHeight/2, this.dotRadius, this.dotVisibilityMillis, this.gc));
                this.update();
                this.started = true;
            } else {
                this.hit(event.getX(), event.getY());
            }
        });
        this.timer.start();
    }

    public void stop() {
        if (this.timer != null) {
            this.timer.stop();
        }
    }
    
    public void reset() {
        this.stop();
        this.started = false;
        this.missedShotCount = 0;
        this.lastHitX = -1;
        this.lastHitY = -1;
        
        this.canvas = new Canvas(this.canvasWidth, this.canvasHeight);
        this.canvas.setLayoutX(this.canvasSafeZone);
        this.canvas.setLayoutY(this.canvasSafeZone);
        
        this.rootPane.getChildren().clear();
        this.rootPane.getChildren().add(this.canvas);
        this.rootPane.setPrefSize(rootWidth, rootHeight);

        this.gc = canvas.getGraphicsContext2D();
        this.setCursor(Cursor.CROSSHAIR);
        this.dots = new ArrayList<>();
        this.rand = new Random();
        this.timer = new AimCanvasTimer(this);
        this.start();
    }

    public void setWidth(double width) {
        this.canvasWidth = width - this.canvasSafeZone*2;
        this.rootWidth = width;
        this.reset();
    }

    public void setHeight(double height) {
        this.canvasHeight = height - this.canvasSafeZone*2;
        this.rootHeight = height;
        this.reset();
    }

    public void setDotRadius(double dotRadius) {
        this.dotRadius = dotRadius;
        this.reset();
    }

    public void setDotVisibilityMillis(long dotVisibilityMillis) {
        this.dotVisibilityMillis = dotVisibilityMillis;
        this.reset();
    }

    public void setMultiDotCount(int multiDotCount) {
        this.multiDotCount = multiDotCount;
        this.reset();
    }

    public void setAddDots(int addDots) {
        this.addDots = addDots;
        this.reset();
    }

    public void setAddDotDelayMillis(long addDotDelayMillis) {
        this.addDotDelayMillis = addDotDelayMillis;
        this.reset();
    }

    public void setMode(String mode) {
        this.mode = mode;
        this.reset();
    }

    public void setPlaySounds(boolean playSounds) {
        this.playSounds = playSounds;
        this.reset();
    }

    private void showResults() {
        VBox resultBox = new VBox();
        resultBox.setLayoutX(this.rootWidth / 2 - 100);
        resultBox.setLayoutY(this.rootHeight / 2 - 110);
        resultBox.setBackground(new Background(new BackgroundFill(Color.rgb(50, 50, 60, 0.8), CornerRadii.EMPTY, Insets.EMPTY)));
        
        Label hitted = new Label("Hitted: " + hittedDotCount());
        hitted.setPadding(new Insets(10));
        hitted.setFont(Font.font(18));
        hitted.setTextFill(Color.GREEN);
        
        Label timeouts = new Label("Missed: " + missedDotCount());
        timeouts.setPadding(new Insets(10));
        timeouts.setFont(Font.font(18));
        timeouts.setTextFill(Color.RED);
        
        Label missed = new Label("Shots missed: " + this.missedShotCount);
        missed.setPadding(new Insets(10));
        missed.setFont(Font.font(18));
        missed.setTextFill(Color.YELLOW);
        
        Button resetButton = new Button("Restart");
        resetButton.setPadding(new Insets(10));
        resetButton.setFont(Font.font(18));
        resetButton.setTextFill(Color.WHITE);
        resetButton.setPrefWidth(160);
        resetButton.setBackground(new Background(new BackgroundFill(Color.rgb(50, 50, 60, 0.8), CornerRadii.EMPTY, Insets.EMPTY)));
        resetButton.setOnAction((event) -> {
            reset();
        });
        
        this.drawResultsToCanvas();
        
        resultBox.getChildren().addAll(hitted, timeouts, missed, resetButton);
        this.rootPane.getChildren().add(resultBox);
    }
    
    private void playSound(String filename) {
        if (!this.playSounds) return;
        try (InputStream inputStream = new BufferedInputStream(getClass().getResourceAsStream("/sounds/" + filename));  AudioInputStream audioStream = AudioSystem.getAudioInputStream(inputStream)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            LOG.error("Cannot play sound!", e);
        }
    }
}
