package com.wakoo.simplechat;

import javafx.scene.control.Alert;

public abstract class MsgDisplay {
    public abstract void DisplayMessage(String msg);
    protected Alert  alert;

    MsgDisplay(String title, String header) {
        this.alert = new Alert(Alert.AlertType.NONE);
        this.alert.setTitle(title);
        this.alert.setHeaderText(header);
    }
}
