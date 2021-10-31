package com.wakoo.simplechat;

import javafx.scene.control.Alert;

public final class ErrorDisplay extends MsgDisplay {
    ErrorDisplay(String title, String header) {
        super(title, header);
        this.alert.setAlertType(Alert.AlertType.ERROR);
    }

    ErrorDisplay() {
        this("Ошибка", "Ошибка");
    }

    ErrorDisplay(String title) {
        this(title, title);
    };

    public void DisplayMessage(String msg) {
        this.alert.setContentText(msg);
        this.alert.showAndWait();
    }
}
