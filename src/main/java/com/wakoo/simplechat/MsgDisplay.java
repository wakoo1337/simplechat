package com.wakoo.simplechat;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class MsgDisplay {
    protected Alert  alert;

    MsgDisplay(String title, String header) {
        Platform.runLater(() -> {
            alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle(title);
            alert.setHeaderText(header);
        });
    }

    public void DisplayMessage(String msg) {
        Platform.runLater(() -> {
            this.alert.setContentText(msg);
            this.alert.showAndWait();
        });
    }
}
