package com.umaplay.androidscreencast;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import javafx.application.Platform;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by user on 10/18/2015.
 */
public class ScreenCaptureThread extends Thread {

    private BufferedImage image;
    private final Dimension size;
    private IDevice device;
    private boolean landscape = false;
    private ScreenCaptureListener listener = null;
    private boolean mRun = true;

    public ScreenCaptureListener getListener() {
        return listener;
    }

    public void setListener(ScreenCaptureListener listener) {
        this.listener = listener;
    }
    public void setDevice(IDevice device) {
        this.device = device;
    }

    public interface ScreenCaptureListener {
        public void handleNewImage(Dimension size, BufferedImage image, boolean landscape);
    }

    public ScreenCaptureThread(IDevice device) {
        super("Screen capture");
        this.device = device;
        image = null;
        size = new Dimension();
    }

    public void run() {
        do {
            try {
                mirror();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    mRun = false;
                }
            } catch (java.nio.channels.ClosedByInterruptException ciex) {
                break;
            } catch (IOException e) {
                System.err.println((new StringBuilder()).append(
                        "Exception fetching image: ").append(e.toString())
                        .toString());
            }
        }
        while(mRun);
    }

    public void kill() {
        mRun = false;
    }

    private void mirror() throws IOException {
        if (device == null) {
            // device not ready
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                mRun = false;
            }
        }

        RawImage rawImage = null;
        synchronized (device) {
            rawImage = device.getScreenshot();
        }

        if (rawImage != null) {
            display(rawImage);
        } else {
            System.err.println("Failed to get screen");
        }
    }

    public void display(RawImage rawImage) {
        int width2 = landscape ? rawImage.height : rawImage.width;
        int height2 = landscape ? rawImage.width : rawImage.height;
        if (image == null) {
            image = new BufferedImage(width2, height2,
                    BufferedImage.TYPE_INT_RGB);
            size.setSize(image.getWidth(), image.getHeight());
        } else {
            if (image.getHeight() != height2 || image.getWidth() != width2) {
                image = new BufferedImage(width2, height2,
                        BufferedImage.TYPE_INT_RGB);
                size.setSize(image.getWidth(), image.getHeight());
            }
        }

        int index = 0;
        int indexInc = rawImage.bpp >> 3;
        for (int y = 0; y < rawImage.height; y++) {
            for (int x = 0; x < rawImage.width; x++, index += indexInc) {
                int value = rawImage.getARGB(index);
                if (landscape)
                    image.setRGB(y, rawImage.width - x - 1, value);
                else
                    image.setRGB(x, y, value);
            }
        }

        if (listener != null) {
            Platform.runLater(new Runnable() {

                public void run() {
                    listener.handleNewImage(size, image, landscape);
                }
            });
        }
    }
}

