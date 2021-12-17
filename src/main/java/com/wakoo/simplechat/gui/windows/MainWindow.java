package com.wakoo.simplechat.gui.windows;

import com.wakoo.simplechat.ProfileCatalog;
import com.wakoo.simplechat.SimpleChat;
import com.wakoo.simplechat.displays.ErrorDisplay;
import com.wakoo.simplechat.displays.InfoDisplay;
import com.wakoo.simplechat.displays.MsgDisplay;
import com.wakoo.simplechat.gui.ChatBox;
import com.wakoo.simplechat.gui.ConnectDisconnectItems;
import com.wakoo.simplechat.gui.UsersBox;
import com.wakoo.simplechat.messages.TextMessage;
import com.wakoo.simplechat.messages.generators.InfoGenerator;
import com.wakoo.simplechat.messages.generators.TextGenerator;
import com.wakoo.simplechat.networking.NetworkingProcessor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Date;
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
        ChatBox.SINGLETON.addMessage(new InfoGenerator("Программа для курсовой работы"));
        ChatBox.SINGLETON.addMessage(new InfoGenerator("Ожидается подключение к порту " + ProfileCatalog.SINGLETON.getListenPort()));
        ConnectDisconnectItems.SINGLETON.setConnectMenuItem(connectMenuItem);
        ConnectDisconnectItems.SINGLETON.setDisconnectMenuItem(disconnectMenuItem);
        ConnectDisconnectItems.SINGLETON.lockConnectDisconnect();
        UsersBox.SINGLETON.setListView(usersListView);
    }

    @FXML
    private void buttonActionSend(ActionEvent event) {
        TextMessage msggen = new TextGenerator(msgField.getText());
        NetworkingProcessor.SINGLETON.sendTo(msggen, true);
        ChatBox.SINGLETON.addMessage(msggen);
    }

    @FXML
    private TextField msgField;

    @FXML
    private MenuItem connectMenuItem, disconnectMenuItem;

    MsgDisplay connerr_disp = new ErrorDisplay("Ошибка при установке или разрыве соединения с сервером");

    @FXML
    private ListView<String> usersListView;

    @FXML
    private void copyAllAction(ActionEvent event) {
        ClipboardContent cc = new ClipboardContent();
        cc.putString(ChatBox.SINGLETON.toString());
        Clipboard.getSystemClipboard().setContent(cc);
    }

    @FXML
    private void saveToFileAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Сохранение истории чата");
        fc.setInitialFileName("Экспорт истории от " + ((new Date())));
        FileChooser.ExtensionFilter sel_filter = new FileChooser.ExtensionFilter("Текстовые документы", "*.txt");
        fc.getExtensionFilters().addAll(
                sel_filter,
                new FileChooser.ExtensionFilter("Все файлы", "*.*"));
        fc.setSelectedExtensionFilter(sel_filter);
        File selected = fc.showSaveDialog(SimpleChat.main_stage);
        if (selected != null) {
            try (BufferedWriter bwr = Files.newBufferedWriter(selected.toPath(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                bwr.write(ChatBox.SINGLETON.toString());
            } catch (IOException ioexcp) {
                (new ErrorDisplay("Ошибка при сохранении текста")).displayMessage(ioexcp, "Невозможно сохранить файл с историей чата");
            }
        }
    }
}
