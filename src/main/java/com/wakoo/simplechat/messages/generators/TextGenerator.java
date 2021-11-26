package com.wakoo.simplechat.messages.generators;

import com.wakoo.simplechat.messages.MessageTypes;
import com.wakoo.simplechat.messages.TextMessage;

public class TextGenerator extends MessageGenerator implements TextMessage {
    final String text;

    public TextGenerator(String text) {
        super(MessageTypes.MessageText);
        this.text = text;
        insertString(text, false);
        finish();
    }

    public String getMarker() {
        return "   ";
    }

    public boolean getSignOk() {
        return true;
    }

    @Override
    public String getText() {
        return text;
    }
}
