package com.wakoo.simplechat.messages.msgs;

import com.wakoo.simplechat.messages.TextMessage;

public interface TextMsg extends TextMessage {
    default String getVisibleText() {
        return "<" + getNickname() + "> " + getValueText();
    }
    default String getMarker() {
        return "   ";
    }
}
