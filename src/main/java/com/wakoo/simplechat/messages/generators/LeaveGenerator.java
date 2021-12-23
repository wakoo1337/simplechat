package com.wakoo.simplechat.messages.generators;

import com.wakoo.simplechat.messages.MessageTypes;
import com.wakoo.simplechat.messages.msgs.LeaveMsg;

public final class LeaveGenerator extends MessageGenerator implements LeaveMsg {
    public LeaveGenerator() {
        super(MessageTypes.MessageLeave);
        finish();
    }
}
