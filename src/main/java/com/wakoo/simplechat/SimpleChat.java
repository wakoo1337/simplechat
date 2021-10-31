package com.wakoo.simplechat;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public final class SimpleChat extends Application {
    private Stage main_stage;
    @Override public void start(Stage stage) throws IOException {
        main_stage = stage;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("SimpleChat.fxml"));
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

    @FXML private void MenuActionConnect(ActionEvent event) {
        event.consume();
    }

    @FXML private void MenuActionExit(ActionEvent event) {
        event.consume();
        main_stage.close();
    }

    private MsgDisplay about = null;

    @FXML private void MenuActionAbout(ActionEvent event) {
        event.consume();
        if (about == null) about = new InfoDisplay("О программе", "О SimpleChat");
        about.DisplayMessage("SimpleChat -- клиент децентрализованного чата для ЛВС");
    }

    @FXML private void MenuActionSettings(ActionEvent event) {
        event.consume();
        Stage settings_stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        URL fxml_url = this.getClass().getResource("SettingsWindow.fxml");
        loader.setLocation(fxml_url);
        try {
            GridPane rootLayout = loader.load();
            settings_stage.setScene(new Scene(rootLayout));
        } catch (IOException ioexcp) {
            MsgDisplay err_display = new ErrorDisplay("Ошибка при загрузке окна настроек");
            err_display.DisplayMessage("Ошибка ввода-вывода FXML-файла");
        };
        settings_stage.initOwner(main_stage);
        settings_stage.initModality(Modality.APPLICATION_MODAL);
        settings_stage.show();
    }
}
