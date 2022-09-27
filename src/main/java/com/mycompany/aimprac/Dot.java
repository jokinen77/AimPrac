/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aimprac;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jokin
 */
public class Dot {
    
    private static final Logger LOG = LoggerFactory.getLogger(Dot.class);

    private double x;
    private double y;
    private double r;

    private GraphicsContext gc;

    private long visibilityMillis;
    private long startMillis;
    private long timeoutMillis;
    private long hittedMillis = 0;
    private long drawResultTimeMillis = 100;

    private Color dotColor = Color.rgb(128, 30, 215, 0.4);
    private Color dotCenterColor = Color.rgb(168, 70, 255);
    private Color dotTimeLeftColor = Color.rgb(128, 30, 215, 0.6);

    public Dot(double x, double y, double r, long visibilityMillis, GraphicsContext gc) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.gc = gc;
        this.visibilityMillis = visibilityMillis;
        this.startMillis = System.currentTimeMillis();
        this.timeoutMillis = startMillis + visibilityMillis;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getR() {
        return r;
    }

    private double getVisibilityProportion() {
        return 1 - (double) (System.currentTimeMillis() - startMillis) / visibilityMillis;
    }

    public void draw() {
        if (shouldDrawResult()) {
            drawResult();
        } else if (isActive()) {
            double visibilityRadius = (this.r - 4) * getVisibilityProportion() + 4;
            this.gc.setFill(dotColor);
            this.gc.fillOval(this.x - this.r, this.y - this.r, this.r * 2, this.r * 2);
            this.gc.setFill(dotTimeLeftColor);
            this.gc.fillOval(this.x - visibilityRadius, this.y - visibilityRadius, visibilityRadius * 2, visibilityRadius * 2);
            this.gc.setFill(dotCenterColor);
            this.gc.fillOval(this.x - 4, this.y - 4, 8, 8);
        }
    }

    public void drawResult() {
        Color resultDotColor = isHitted() ? Color.rgb(80, 255, 80, 0.4) : Color.rgb(200, 0, 0, 0.4);
        Color resultDotCenterColor = isHitted() ? Color.rgb(110, 255, 110) : Color.rgb(200, 50, 50);
        this.gc.setFill(resultDotColor);
        this.gc.fillOval(this.x - this.r, this.y - this.r, this.r * 2, this.r * 2);
        this.gc.setFill(resultDotCenterColor);
        this.gc.fillOval(this.x - 4, this.y - 4, 8, 8);
    }

    public boolean hit(double x, double y) {
        if (isTimeouted() || isHitted()) {
            return false;
        }
        if (Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2)) <= this.r) {
            this.hittedMillis = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public boolean isTimeouted() {
        return timeoutMillis < System.currentTimeMillis();
    }

    public boolean isHitted() {
        return hittedMillis > 0;
    }

    private boolean shouldDrawResult() {
        if (isHitted()) {
            return hittedMillis + drawResultTimeMillis > System.currentTimeMillis();
        }
        if (isTimeouted()) {
            return timeoutMillis + drawResultTimeMillis > System.currentTimeMillis();
        }
        return false;
    }

    public boolean isActive() {
        return !isHitted() && !isTimeouted();
    }

    public long getHittedMillis() {
        return hittedMillis;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public long getStartMillis() {
        return startMillis;
    }

}
