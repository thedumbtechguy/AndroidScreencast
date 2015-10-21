package com.umaplay.androidscreencast;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.umaplay.androidscreencast.ui.Dialogs;
import com.umaplay.androidscreencast.ui.scene.DeviceScene;
import com.umaplay.androidscreencast.ui.scene.LoadingScene;
import com.umaplay.androidscreencast.util.DeviceListHelper;
import javafx.application.Application;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Main extends Application implements DeviceListHelper.DeviceListListener, ScreenCaptureThread.ScreenCaptureListener {

    private Stage mStage;
    private IDevice mDevice;
    private LoadingScene mLoadingScene;
    private ScreenCaptureThread mScreenCaptureThread;
    private DeviceScene mDeviceScene;
    private Dimension oldSize;

    @Override
    public void start(Stage primaryStage) throws Exception{
        mStage = primaryStage;
        mStage.setTitle("Android Screencast");
        mStage.setResizable(false);
        mStage.setWidth(400);
        mStage.setHeight(600);


        mLoadingScene = new LoadingScene();
        mDeviceScene = new DeviceScene();


        mStage.setScene(mLoadingScene);
        mStage.show();


        DeviceListHelper.RefreshList(AndroidDebugBridge.createBridge(), this);
    }



    @Override
    public void stop() throws Exception {
        if(mScreenCaptureThread != null)
            mScreenCaptureThread.kill();

        AndroidDebugBridge.terminate();
        super.stop();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void onLoadStart() {
        mStage.setScene(mLoadingScene);
        mLoadingScene.progressText.setText("Retrieving device list...");
        mLoadingScene.progressBar.setVisible(true);
        mLoadingScene.button.setVisible(false);
    }

    @Override
    public void onLoadEnd(AndroidDebugBridge bridge) {
        mStage.setScene(mLoadingScene);
        mLoadingScene.progressBar.setVisible(false);

        IDevice[] devices = bridge.getDevices();

        if(devices.length == 1) {
            mDevice = devices[0];
            mLoadingScene.progressText.setText("Connected to: " + mDevice.getSerialNumber());
        }
        else if(devices.length > 1) {
            mDevice = Dialogs.DisplayDeviceList(devices);
            if(mDevice == null) {
                mLoadingScene.progressText.setText("No device selected!");
                mLoadingScene.button.setText("Try Again");
                mLoadingScene.button.setOnAction(e -> { DeviceListHelper.RefreshList(AndroidDebugBridge.createBridge(), this); });
                mLoadingScene.button.setVisible(true);
            }
            else
                mLoadingScene.progressText.setText("Connected to: " + mDevice.getSerialNumber());
        }
        else {
            mDevice = null;
            mLoadingScene.progressText.setText("No devices found!");
            mLoadingScene.button.setText("Try again");
            mLoadingScene.button.setOnAction(e -> { DeviceListHelper.RefreshList(AndroidDebugBridge.createBridge(), this); });
            mLoadingScene.button.setVisible(true);
        }

        if(mDevice != null) {
            mDeviceScene.setDevice(mDevice);
            mStage.setScene(mDeviceScene);

            if(mScreenCaptureThread == null) {
                mScreenCaptureThread = new ScreenCaptureThread(mDevice);
                mScreenCaptureThread.setListener(this);
            }

            mScreenCaptureThread.setDevice(mDevice);
            mScreenCaptureThread.start();
        }
    }

    @Override
    public void onLoadTimeout() {
        mStage.setScene(mLoadingScene);
        mLoadingScene.progressText.setText("Failed to load devices. Timed out!");
        mLoadingScene.progressBar.setVisible(false);
        mLoadingScene.button.setText("Try again");
        mLoadingScene.button.setOnAction(e -> { DeviceListHelper.RefreshList(AndroidDebugBridge.createBridge(), this); });
        mLoadingScene.button.setVisible(true);
    }

    @Override
    public void handleNewImage(Dimension size, BufferedImage image, boolean landscape) {
        if(oldSize == null || !size.equals(oldSize)) {
            double factor = 700.0/size.height;
            double height = (size.height * factor) + 30;
            double width = size.width * factor;

            mStage.setWidth(width);
            mStage.setHeight(height);
            oldSize = size;
        }

        mDeviceScene.handleNewImage(size, image, landscape);
    }

}
