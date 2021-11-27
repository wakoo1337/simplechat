package com.wakoo.simplechat.gui.windows;

import com.wakoo.simplechat.ProfileCatalog;
import com.wakoo.simplechat.displays.ErrorDisplay;
import com.wakoo.simplechat.displays.MsgDisplay;
import com.wakoo.simplechat.networking.NetworkingProcessor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public final class ConnectWindow implements Initializable {
    @FXML
    private GridPane windowPane;
    @FXML
    private Spinner<Integer> portSpinner;
    @FXML
    private TextField netAddress;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        portSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 65535, 1)); /* Это должно было быть в FXML, но там не работает */
        portSpinner.getValueFactory().setValue(ProfileCatalog.SINGLETON.getListenPort());
    }

    public void onConnect(ActionEvent event) {
        try {
            NetworkingProcessor.ServerConnection.SINGLETON.connectServer(InetAddress.getByName(netAddress.getText()), portSpinner.getValueFactory().getValue());
        } catch (UnknownHostException uhostexcp) {
            err.displayMessage(uhostexcp, "Нет такого хоста");
        } catch (IOException ioexcp) {
            err.displayMessage(ioexcp, "Ошибка ввода-вывода");
        }
    }

    public void onCancel(ActionEvent event) {
        ((Stage) windowPane.getScene().getWindow()).close();
    }

    MsgDisplay err = new ErrorDisplay("Ошибка при соединении с сервером");
}
