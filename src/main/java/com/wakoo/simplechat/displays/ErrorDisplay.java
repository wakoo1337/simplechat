package com.wakoo.simplechat.displays;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public final class ErrorDisplay extends MsgDisplay {
    public ErrorDisplay(String title, String header) {
        super(title, header);
        Platform.runLater(() -> {
            this.alert.setAlertType(Alert.AlertType.ERROR);
        });
    }

    public ErrorDisplay() {
        this("Ошибка", "Ошибка");
    }

    public ErrorDisplay(String title) {
        this(title, title);
    }
}
