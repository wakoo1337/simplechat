package com.wakoo.simplechat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public final class LeaveProcessor extends MessageProcessor {
    public final String marker = "<<<";

    LeaveProcessor(InetSocketAddress addr, byte[] okey, byte[] sign, ByteBuffer remain) {
        super(addr, okey, sign, remain);
        ChatBox.SINGLETON.AddMessage("Из чата вышел " + nickname, this);
    }
}
