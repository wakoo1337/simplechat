package com.wakoo.simplechat.gui.windows;

import com.wakoo.simplechat.ProfileCatalog;
import com.wakoo.simplechat.SimpleChat;
import com.wakoo.simplechat.displays.ErrorDisplay;
import com.wakoo.simplechat.displays.InfoDisplay;
import com.wakoo.simplechat.displays.MsgDisplay;
import com.wakoo.simplechat.gui.ChatBox;
import com.wakoo.simplechat.gui.ConnectDisconnectItems;
import com.wakoo.simplechat.messages.TextMessage;
import com.wakoo.simplechat.messages.generators.InfoGenerator;
import com.wakoo.simplechat.messages.generators.MessageGenerator;
import com.wakoo.simplechat.messages.generators.TextGenerator;
import com.wakoo.simplechat.networking.NetworkingProcessor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public final class MainWindow implements Initializable {
    @FXML
    private void menuActionConnect(ActionEvent event) {
        event.consume();
        Stage connect_stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        URL fxml_url = this.getClass().getResource("/com/wakoo/simplechat/ConnectWindow.fxml");
        loader.setLocation(fxml_url);
        try {
            GridPane rootLayout = loader.load();
            connect_stage.setScene(new Scene(rootLayout));
        } catch (IOException ioexcp) {
            connerr_disp.displayMessage(ioexcp, "Ошибка ввода-вывода FXML-файла окна подключения к сервера");
        }
        connect_stage.initOwner(SimpleChat.main_stage);
        connect_stage.initModality(Modality.APPLICATION_MODAL);
        connect_stage.showAndWait();

    }

    @FXML
    private void menuActionDisconnect(ActionEvent event) {
        try {
            NetworkingProcessor.ServerConnection.SINGLETON.disconnectServer();
        } catch (IOException ioexcp) {
            connerr_disp.displayMessage(ioexcp, "Невозможно отключиться от сервера");
        }
    }

    @FXML
    private void menuActionExit(ActionEvent event) {
        event.consume();
        NetworkingProcessor.SINGLETON.stopIt();
        SimpleChat.main_stage.close();
    }

    @FXML
    private void menuActionAbout(ActionEvent event) {

        event.consume();
        MsgDisplay about = new InfoDisplay("О программе", "О SimpleChat");
        about.displayMessage("SimpleChat -- клиент децентрализованного чата для ЛВС");
    }

    @FXML
    private void menuActionSettings(ActionEvent event) {
        event.consume();
        Stage settings_stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        URL fxml_url = this.getClass().getResource("/com/wakoo/simplechat/SettingsWindow.fxml");
        loader.setLocation(fxml_url);
        try {
            GridPane rootLayout = loader.load();
            settings_stage.setScene(new Scene(rootLayout));
        } catch (IOException ioexcp) {
            MsgDisplay err_display = new ErrorDisplay("Ошибка при загрузке окна настроек");
            err_display.displayMessage("Ошибка ввода-вывода FXML-файла");
        }
        settings_stage.initOwner(SimpleChat.main_stage);
        settings_stage.initModality(Modality.APPLICATION_MODAL);
        settings_stage.show();
    }

    @FXML
    private TextArea chatTextArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ChatBox.SINGLETON.setArea(chatTextArea);
        ChatBox.SINGLETON.addMessage("SimpleChat -- Программа для курсовой работы", new InfoGenerator());
        ChatBox.SINGLETON.addMessage("Ожидается подключение к порту " + ProfileCatalog.SINGLETON.getListenPort(), new InfoGenerator());
        ConnectDisconnectItems.SINGLETON.setConnectMenuItem(connectMenuItem);
        ConnectDisconnectItems.SINGLETON.setDisconnectMenuItem(disconnectMenuItem);
        ConnectDisconnectItems.SINGLETON.lockConnectDisconnect();
    }

    @FXML
    private void buttonActionSend(ActionEvent event) {
        TextMessage msggen = new TextGenerator(msgField.getText());
        NetworkingProcessor.SINGLETON.sendTo(msggen, true);
        ChatBox.SINGLETON.addMessage(msggen.getText(), msggen);
    }

    @FXML
    private TextField msgField;

    @FXML
    private MenuItem connectMenuItem, disconnectMenuItem;

    MsgDisplay connerr_disp = new ErrorDisplay("Ошибка при установке или разрыве соединения с сервером");
}
