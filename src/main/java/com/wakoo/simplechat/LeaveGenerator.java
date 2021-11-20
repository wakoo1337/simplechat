package com.wakoo.simplechat;

public class LeaveGenerator extends MessageGenerator {
    LeaveGenerator() {
        super(MessageDispatcher.MessageTypes.MessageLeave);
        Finish();
    }
}
