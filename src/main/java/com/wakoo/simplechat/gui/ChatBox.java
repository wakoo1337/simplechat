package com.wakoo.simplechat.gui;

import com.wakoo.simplechat.messages.Message;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.util.Date;

public final class ChatBox {
    public static final ChatBox SINGLETON = new ChatBox();
    private static final StringBuilder sb = new StringBuilder();
    private TextArea area;

    public void setArea(TextArea area) {
        this.area = area;
    }

    public void addMessage(final String msg, final Message msgproc) {
        Platform.runLater(() -> {
            sb.append(msgproc.getMarker());
            sb.append(" ");
            sb.append((new Date()).toString());
            sb.append(" [");
            sb.append(msgproc.getSignOk() ? "\uD83D\uDD12" : "\uD83D\uDD13");
            sb.append("] ");
            sb.append(msg);
            sb.append("\n");
            area.setText(sb.toString());
        });
    }
}
