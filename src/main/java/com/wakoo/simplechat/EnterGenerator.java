package com.wakoo.simplechat;

public class EnterGenerator extends MessageGenerator {
    EnterGenerator() {
        super(MessageDispatcher.MessageTypes.MessageEnter);
        Finish();
    }
}
