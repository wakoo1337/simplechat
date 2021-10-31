package com.wakoo.simplechat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public final class SettingsWindow {
    @FXML private TextField nicknameBox;

    @FXML  private void resetClick(ActionEvent event) {
        nicknameBox.setText(SettingsStorage.SINGLETON.getNickname());
    }

    @FXML private void okClick(ActionEvent event) {
        SettingsStorage.SINGLETON.setNickname(nicknameBox.getText());
    }
}
