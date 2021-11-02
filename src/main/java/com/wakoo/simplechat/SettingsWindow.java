package com.wakoo.simplechat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public final class SettingsWindow implements Initializable {

    @FXML private TextField nicknameBox;
    @FXML private Spinner<Integer> portBox;

    @FXML  private void resetClick(ActionEvent event) {
        event.consume();
        reset();
    }

    @FXML private void okClick(ActionEvent event) {
        event.consume();
        SettingsStorage.SINGLETON.setNickname(nicknameBox.getText());
        SettingsStorage.SINGLETON.setListenPort(portBox.getValue());
    }

    @FXML private void newKeyPairClick(ActionEvent event) {
        event.consume();
        SettingsStorage.SINGLETON.NewKeyPair();
    }

    private void reset() {
        nicknameBox.setText(SettingsStorage.SINGLETON.getNickname());
        portBox.getValueFactory().setValue(SettingsStorage.SINGLETON.getListenPort());
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        portBox.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 65535, 1)); /* Это должно было быть в FXML, но там не работает */
        reset();
    }
}
