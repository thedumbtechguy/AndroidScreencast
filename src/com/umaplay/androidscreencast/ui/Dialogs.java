package com.umaplay.androidscreencast.ui;

import com.android.ddmlib.IDevice;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Dialogs {

    private static IDevice selectedDevice;
    public static IDevice DisplayDeviceList(IDevice[] devices) {
        Stage stage = new Stage();
        stage.setTitle("Select Device");
        stage.setResizable(false);
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 10, 30, 10));
        layout.setAlignment(Pos.CENTER);

        selectedDevice = null;
        for(IDevice device: devices) {
            Button button = new Button(device.getSerialNumber() + ": " + device.getState());
            button.setOnAction(e -> {
                selectedDevice = device;
                stage.close();
            });
            layout.getChildren().add(button);
        }


        Scene scene = new Scene(layout);

        stage.setScene(scene);
        stage.showAndWait();

        return selectedDevice;
    }
}
