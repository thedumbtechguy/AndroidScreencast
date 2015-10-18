package com.umaplay.androidscreencast.ui.scene;

import com.android.ddmlib.IDevice;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoadingScene extends Scene {

    public final ProgressBar progressBar;
    public final Label progressText;
    public final Button button;

    public LoadingScene() {
        super(new VBox(5));

        VBox layout = (VBox) this.getRoot();

        progressBar = new ProgressBar();
        progressText = new Label();
        button = new Button();

        layout.getChildren().addAll(progressBar, progressText, button);
        layout.setAlignment(Pos.CENTER);
    }



}
