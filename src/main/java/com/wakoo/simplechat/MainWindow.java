package com.wakoo.simplechat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public final class MainWindow {
    @FXML private void MenuActionConnect(ActionEvent event) {
        event.consume();
        Stage connect_stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        URL fxml_url = this.getClass().getResource("ConnectWindow.fxml");
        loader.setLocation(fxml_url);
        try {
            GridPane rootLayout = loader.load();
            connect_stage.setScene(new Scene(rootLayout));
        } catch (IOException ioexcp) {
            MsgDisplay err_display = new ErrorDisplay("Ошибка при загрузке окна настроек");
            err_display.DisplayMessage("Ошибка ввода-вывода FXML-файла");
        }
        connect_stage.initOwner(SimpleChat.main_stage);
        connect_stage.initModality(Modality.APPLICATION_MODAL);
        connect_stage.show();
    }

    @FXML private void MenuActionExit(ActionEvent event) {
        event.consume();
        SimpleChat.main_stage.close();
    }

    @FXML
    private void MenuActionAbout(ActionEvent event) {

        event.consume();
        MsgDisplay about = new InfoDisplay("О программе", "О SimpleChat");
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
        }
        settings_stage.initOwner(SimpleChat.main_stage);
        settings_stage.initModality(Modality.APPLICATION_MODAL);
        settings_stage.show();
    }
}
