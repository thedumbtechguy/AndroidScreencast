package com.umaplay.androidscreencast.ui.scene;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.umaplay.androidscreencast.ScreenCaptureThread;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.SwipeEvent;
import javafx.scene.paint.Color;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class DeviceScene extends Scene implements ScreenCaptureThread.ScreenCaptureListener {
    private final Group layout;
    private final ImageView imageView;
    private final Canvas canvas;
    private long pressedTime;
    private double pressedX;
    private double pressedY;
    private boolean dragging = false;
    private Dimension oldSize;
    private double scaleDownFactor;
    private double scaleUpFactor;
    private IDevice device;

//    public final ProgressBar progressBar;
//    public final Label progressText;
//    public final Button button;

    public DeviceScene() {
        super(new Group());

        layout = (Group) this.getRoot();
        imageView = new ImageView();
        canvas = new Canvas();
        layout.getChildren().add(canvas);


        canvas.setOnMouseDragged(e -> {
            dragging = true;
        });

        canvas.setOnMousePressed(e -> {
            pressedX = e.getX() * scaleUpFactor;
            pressedY = e.getY() * scaleUpFactor;

            pressedTime = System.currentTimeMillis();
        });

        canvas.setOnMouseReleased(e -> {
            double releasedX = e.getX() * scaleUpFactor;
            double releasedY = e.getY() * scaleUpFactor;

            String command;
            if(dragging) {
                long dragDuration = System.currentTimeMillis() - pressedTime;
                command = String.format("input touchscreen swipe %s %s %s %s %s", pressedX, pressedY, releasedX, releasedY, dragDuration);
            }
            else {
                command = String.format("input touchscreen tap %s %s", releasedX, releasedY);
            }

            try {
                device.executeShellCommand(command, new IShellOutputReceiver() {
                    @Override
                    public void addOutput(byte[] bytes, int i, int i1) {

                    }

                    @Override
                    public void flush() {

                    }

                    @Override
                    public boolean isCancelled() {
                        return false;
                    }
                });
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            pressedTime = 0;
            dragging = false;
            pressedX = pressedY = 0;
        });

//        canvas.setOnMouseClicked(e -> {


//            double x = e.getX() * scaleUpFactor;
//            double y = e.getY() * scaleUpFactor;
//
//            System.out.println(String.format("Clicked:- %s : %s", x, y));
//            try {
//                device.executeShellCommand(String.format("input touchscreen tap %s %s", x, y), new IShellOutputReceiver() {
//                    @Override
//                    public void addOutput(byte[] bytes, int i, int i1) {
//
//                    }
//
//                    @Override
//                    public void flush() {
//
//                    }
//
//                    @Override
//                    public boolean isCancelled() {
//                        return false;
//                    }
//                });
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//        });


//        canvas.setOnMouseDragExited(e -> {
//            if(dragging) {
//                double dragEndX = e.getX() * scaleUpFactor;
//                double dragEndY = e.getY() * scaleUpFactor;
//
//                try {
//                    device.executeShellCommand(String.format("input touchscreen swipe %s %s %s %s %s", dragStartX, dragStartY, dragEndX, dragEndY, 100), new IShellOutputReceiver() {
//                        @Override
//                        public void addOutput(byte[] bytes, int i, int i1) {
//
//                        }
//
//                        @Override
//                        public void flush() {
//
//                        }
//
//                        @Override
//                        public boolean isCancelled() {
//                            return false;
//                        }
//                    });
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//            }
//            dragging = false;
//        });
    }


    @Override
    public void handleNewImage(Dimension size, BufferedImage image, boolean landscape) {
        double height = size.height * scaleDownFactor;
        double width = size.width * scaleDownFactor;

        if(oldSize == null || !size.equals(oldSize)) {
            scaleDownFactor = 700.0/size.height;
            scaleUpFactor = size.height/700.0;

            height = size.height * scaleDownFactor;
            width = size.width * scaleDownFactor;

            canvas.setWidth(width);
            canvas.setHeight(height);
            oldSize = size;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        WritableImage fxImage = new WritableImage(size.width, size.height);
        SwingFXUtils.toFXImage(image, fxImage);
        gc.drawImage(fxImage, 0, 0, width, height);
    }

    public void setDevice(IDevice device) {
        this.device = device;
    }
}
