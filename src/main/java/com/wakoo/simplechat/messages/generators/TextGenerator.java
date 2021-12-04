package com.wakoo.simplechat.messages.generators;

import com.wakoo.simplechat.messages.MessageTypes;
import com.wakoo.simplechat.messages.msgs.TextMsg;

public class TextGenerator extends MessageGenerator implements TextMsg {
    final String text;

    public TextGenerator(String text) {
        super(MessageTypes.MessageText);
        insertString(text, false);
        finish();
        this.text = text;
    }

    public String getValueText() {return text;}
}
