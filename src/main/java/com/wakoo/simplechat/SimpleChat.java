package com.wakoo.simplechat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.IOException;

public final class SimpleChat extends Application {
    public static Stage main_stage;
    @Override public void start(Stage stage) {
        main_stage = stage;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("MainWindow.fxml"));
        try {
            GridPane rootLayout = loader.load();

            Scene scene = new Scene(rootLayout);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ioexcp) {
            MsgDisplay err = new ErrorDisplay("Ошибка при загрузке главного окна");
            err.DisplayMessage("Невозможно загрузить FXML-файл главного окна");
        }
    }

    public void stop() {
        ProfileCatalog.SINGLETON.SaveSettings();
    }

    public static void main(String[] args) {
        launch();
    }

    public void init() {
        /* TODO тут начать слушать входящие соединения */
    }
}
