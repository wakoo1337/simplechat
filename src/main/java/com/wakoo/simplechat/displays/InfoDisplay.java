package com.wakoo.simplechat.displays;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public final class InfoDisplay extends MsgDisplay {
    public InfoDisplay(String title, String header) {
        super(title, header);
        Platform.runLater(() -> {
            this.alert.setAlertType(Alert.AlertType.INFORMATION);
        });
    }

    public InfoDisplay() {
        this("Сообщение", "Сообщение");
    }

    public InfoDisplay(String title) {
        this(title, title);
    }
}
