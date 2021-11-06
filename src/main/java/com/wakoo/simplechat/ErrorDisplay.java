package com.wakoo.simplechat;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public final class ErrorDisplay extends MsgDisplay {
    ErrorDisplay(String title, String header) {
        super(title, header);
        Platform.runLater(() -> {
            this.alert.setAlertType(Alert.AlertType.ERROR);
        });
    }

    ErrorDisplay() {
        this("Ошибка", "Ошибка");
    }

    ErrorDisplay(String title) {
        this(title, title);
    }
}
