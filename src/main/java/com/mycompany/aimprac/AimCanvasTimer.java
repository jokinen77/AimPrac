/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aimprac;

import javafx.animation.AnimationTimer;

/**
 *
 * @author jokin
 */
public class AimCanvasTimer extends AnimationTimer {
    private AimCanvas canvas;

    public AimCanvasTimer(AimCanvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void handle(long l) {
        canvas.update();
    }
}
