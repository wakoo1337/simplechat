package com.wakoo.simplechat;

import com.wakoo.simplechat.displays.ErrorDisplay;
import com.wakoo.simplechat.displays.MsgDisplay;
import com.wakoo.simplechat.networking.NetworkingProcessor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class SimpleChat extends Application {
    public static Stage main_stage;

    @Override
    public void start(Stage stage) {
        Platform.setImplicitExit(true);
        final NetworkingProcessor futile = NetworkingProcessor.SINGLETON; // FIXME запускаем синглтонный тред дико омским способом
        stage.setOnCloseRequest(close -> {
            NetworkingProcessor.SINGLETON.stopIt();
        });
        main_stage = stage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/com/wakoo/simplechat/MainWindow.fxml"));
        try {
            Parent rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ioexcp) {
            MsgDisplay err = new ErrorDisplay("Ошибка главного окна");
            err.displayMessage(ioexcp, "Невозможно загрузить FXML-файл главного окна");
        }
    }

    public void stop() {
        ProfileCatalog.SINGLETON.saveSettings();
    }

    public static void main(String[] args) {
        launch();
    }
}
