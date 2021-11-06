package com.wakoo.simplechat;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public final class InfoDisplay extends MsgDisplay {
    InfoDisplay(String title, String header) {
        super(title, header);
        Platform.runLater(() -> {
            this.alert.setAlertType(Alert.AlertType.INFORMATION);
        });
    }

    InfoDisplay() {
        this("Сообщение", "Сообщение");
    }

    InfoDisplay(String title) {
        this(title, title);
    }
}
