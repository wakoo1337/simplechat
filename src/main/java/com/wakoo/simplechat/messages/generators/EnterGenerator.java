package com.wakoo.simplechat.messages.generators;

import com.wakoo.simplechat.messages.MessageTypes;

public final class EnterGenerator extends MessageGenerator {
    EnterGenerator() {
        super(MessageTypes.MessageEnter);
        finish();
    }

    public String getMarker() {
        return ">>>";
    }

    public boolean getSignOk() {
        return true;
    }
}
