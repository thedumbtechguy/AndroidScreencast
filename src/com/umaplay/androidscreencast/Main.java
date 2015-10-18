package com.umaplay.androidscreencast;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.umaplay.androidscreencast.ui.DeviceListDialog;
import com.umaplay.androidscreencast.ui.scene.DeviceScene;
import com.umaplay.androidscreencast.ui.scene.LoadingScene;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Main extends Application implements DeviceListHelper.DeviceListListener, ScreenCaptureThread.ScreenCaptureListener {

    private Stage mWindow;
    private IDevice mDevice;
    private LoadingScene mLoadingScene;
    private ScreenCaptureThread mScreenCaptureThread;
    private DeviceScene mDeviceScene;
    private Dimension oldSize;

    @Override
    public void start(Stage primaryStage) throws Exception{
        mWindow = primaryStage;
        mWindow.setTitle("Android Screencast");
        mWindow.setResizable(false);
        mWindow.setWidth(400);
        mWindow.setHeight(600);



        mLoadingScene = new LoadingScene();
        mDeviceScene = new DeviceScene();

//        Parent root = FXMLLoader.load(getClass().getResource("ui/Main.fxml"));


        mWindow.setScene(mLoadingScene);
        mWindow.show();


        DeviceListHelper.RefreshList(AndroidDebugBridge.createBridge(), this);
    }



    @Override
    public void stop() throws Exception {

//        if(injector != null)
//            injector.close();

        if(mScreenCaptureThread != null)
            mScreenCaptureThread.join();

        AndroidDebugBridge.terminate();
        super.stop();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void onLoadStart() {
        mWindow.setScene(mLoadingScene);
        mLoadingScene.progressText.setText("Retrieving device list...");
        mLoadingScene.progressBar.setVisible(true);
        mLoadingScene.button.setVisible(false);
    }

    @Override
    public void onLoadEnd(AndroidDebugBridge bridge) {
        mWindow.setScene(mLoadingScene);
        mLoadingScene.progressBar.setVisible(false);

        IDevice[] devices = bridge.getDevices();

        if(devices.length == 1) {
            mDevice = devices[0];
            mLoadingScene.progressText.setText("Connected to: " + mDevice.getSerialNumber());
        }
        else if(devices.length > 1) {
            mDevice = DeviceListDialog.display(devices);
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
            mWindow.setScene(mDeviceScene);

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
        mWindow.setScene(mLoadingScene);
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

            mWindow.setWidth(width);
            mWindow.setHeight(height);
            oldSize = size;
        }

        mDeviceScene.handleNewImage(size, image, landscape);
    }

}
