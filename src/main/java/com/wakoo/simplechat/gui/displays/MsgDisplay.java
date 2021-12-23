package com.wakoo.simplechat.gui.displays;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class MsgDisplay {
    protected Alert alert;

    protected MsgDisplay(String title, String header) {
        defaultHeader = header;
        Platform.runLater(() -> {
            alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle(title);
            alert.setHeaderText(header);
        });
    }

    public void displayMessage(String msg) {
        Platform.runLater(() -> {
            this.alert.setContentText(msg);
            this.alert.showAndWait();
        });
    }

    private final String defaultHeader;

    public void displayMessage(Throwable excp, String header) {
        Platform.runLater(() -> {
            this.alert.setHeaderText(header);
            this.alert.setContentText(excp.getMessage());
            this.alert.showAndWait();
            this.alert.setHeaderText(defaultHeader);
        });
    }
}
