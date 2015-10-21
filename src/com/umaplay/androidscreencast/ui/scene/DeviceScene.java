package com.umaplay.androidscreencast.ui.scene;

import com.android.ddmlib.IDevice;
import com.umaplay.androidscreencast.ScreenCaptureThread;
import com.umaplay.androidscreencast.util.Command;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class DeviceScene extends Scene implements ScreenCaptureThread.ScreenCaptureListener {
    private final Canvas mCanvas;
    private Dimension mSize;
    private IDevice mDevice;
    private double mScaleDownFactor;
    private double mScaleUpFactor;
    private long _pressedTime;
    private double _pressedX;
    private double _pressedY;
    private boolean _dragging;

    public DeviceScene() {
        super(new Group());

        mCanvas = setupCanvas();


        ((Group) this.getRoot()).getChildren().add(mCanvas);

        mCanvas.requestFocus();
    }

    private Canvas setupCanvas() {
        Canvas canvas = new Canvas();

        //we don't have a proper dragged event that gives us start and end
        //also, a click is fired at the end of what is considered a drag so we can't rely on the inbuilt event
        //we have to create our own

        canvas.setOnMousePressed(e -> {
            _pressedX = e.getX() * mScaleUpFactor;
            _pressedY = e.getY() * mScaleUpFactor;

            _pressedTime = System.currentTimeMillis();
        });

        canvas.setOnMouseDragged(e -> {
            _dragging = true;
        });

        canvas.setOnMouseReleased(e -> {
            canvas.requestFocus();

            double releasedX = e.getX() * mScaleUpFactor;
            double releasedY = e.getY() * mScaleUpFactor;

            String command;
            if (_dragging) {
                long dragDuration = System.currentTimeMillis() - _pressedTime;
                command = String.format("input touchscreen swipe %s %s %s %s %s", _pressedX, _pressedY, releasedX, releasedY, dragDuration);
            } else {
                command = String.format("input touchscreen tap %s %s", releasedX, releasedY);
            }

            try {
                Command.send(mDevice, command);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            _pressedTime = 0;
            _dragging = false;
            _pressedX = _pressedY = 0;
        });

        return canvas;
    }

    @Override
    public void handleNewImage(Dimension size, BufferedImage image, boolean landscape) {
        double height = size.height * mScaleDownFactor;
        double width = size.width * mScaleDownFactor;

        if(mSize == null || !size.equals(mSize)) {
            mScaleDownFactor = 700.0/size.height;
            mScaleUpFactor = size.height/700.0;

            height = size.height * mScaleDownFactor;
            width = size.width * mScaleDownFactor;

            mCanvas.setWidth(width);
            mCanvas.setHeight(height);
            mSize = size;
        }

        GraphicsContext gc = mCanvas.getGraphicsContext2D();
        WritableImage fxImage = new WritableImage(size.width, size.height);
        SwingFXUtils.toFXImage(image, fxImage);
        gc.drawImage(fxImage, 0, 0, width, height);
    }

    public void setDevice(IDevice device) {
        this.mDevice = device;
    }
}
