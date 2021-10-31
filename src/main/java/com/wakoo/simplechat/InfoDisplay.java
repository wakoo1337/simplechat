package com.wakoo.simplechat;

import javafx.scene.control.Alert;

public final class InfoDisplay extends MsgDisplay {
    InfoDisplay(String title, String header) {
        super(title, header);
        this.alert.setAlertType(Alert.AlertType.INFORMATION);
    }

    InfoDisplay() {
        this("Сообщение", "Сообщение");
    }

    InfoDisplay(String title) {
        this(title, title);
    };

    public void DisplayMessage(String msg) {
        this.alert.setContentText(msg);
        this.alert.showAndWait();
    }
}
