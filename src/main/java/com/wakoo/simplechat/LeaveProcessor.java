package com.wakoo.simplechat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public final class LeaveProcessor extends MessageProcessor {
    public final String marker = "<<<";

    LeaveProcessor(final int type, final ByteBuffer msg) {
        super(type, msg);
        ChatBox.SINGLETON.AddMessage("Из чата вышел " + nickname, this);
    }
}
