package com.wakoo.simplechat.messages.generators;

import com.wakoo.simplechat.messages.MessageTypes;
import com.wakoo.simplechat.messages.msgs.EnterMsg;

public final class EnterGenerator extends MessageGenerator implements EnterMsg {
    public EnterGenerator() {
        super(MessageTypes.MessageEnter);
        finish();
    }
}
