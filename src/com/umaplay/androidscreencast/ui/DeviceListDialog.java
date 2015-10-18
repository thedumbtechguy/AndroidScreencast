package com.umaplay.androidscreencast.ui;

import com.android.ddmlib.IDevice;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DeviceListDialog {
    private static IDevice selectedDevice;

    public static IDevice display(IDevice[] devices) {
        Stage window = new Stage();
        window.setTitle("Select Device");
        window.setResizable(false);
        window.initStyle(StageStyle.UTILITY);
        window.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 10, 30, 10));
        layout.setAlignment(Pos.CENTER);

        selectedDevice = null;
        for(IDevice device: devices) {
            Button button = new Button(device.getSerialNumber() + ": " + device.getState());
            button.setOnAction(e -> {
                selectedDevice = device;
                window.close();
            });
            layout.getChildren().add(button);
        }


        Scene scene = new Scene(layout);

        window.setScene(scene);
        window.showAndWait();

        return selectedDevice;
    }
}
