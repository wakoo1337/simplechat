package com.wakoo.simplechat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SimpleChat extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SimpleChat.class.getResource("SimpleChat.fxml"));
        GridPane rootLayout = loader.load();
        Scene scene = new Scene(rootLayout);
        stage.setScene(scene);
        stage.show();
    }

    public void stop() {
        SettingsStorage.SINGLETON.SaveSettings();
    }

    public static void main(String[] args) {
        launch();
    }
}
