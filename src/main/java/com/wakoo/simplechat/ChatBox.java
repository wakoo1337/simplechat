package com.wakoo.simplechat;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.util.Date;

public final class ChatBox {
    public static final ChatBox SINGLETON = new ChatBox();
    private static final StringBuilder sb = new StringBuilder();
    private TextArea area;
    public void SetArea(TextArea area) {
        this.area = area;
    }
    public void AddMessage(final String msg, final MessageProcessor msgproc) {
        Platform.runLater(() -> {
            sb.append(msgproc.marker);
            sb.append(" ");
            sb.append((new Date()).toString());
            sb.append(" [");
            sb.append(msgproc.GetSignOk() ? "\uD83D\uDD12" : "\uD83D\uDD13");
            sb.append("] ");
            sb.append(msg);
            sb.append("\n");
            area.setText(sb.toString());
        });
    }
}
