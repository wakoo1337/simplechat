package com.wakoo.simplechat.messages.generators;

import com.wakoo.simplechat.messages.MessageTypes;

public class LeaveGenerator extends MessageGenerator {
    public String getMarker() {
        return "<<<";
    }

    LeaveGenerator() {
        super(MessageTypes.MessageLeave);
        finish();
    }
    public boolean getSignOk() {return true;}
}
