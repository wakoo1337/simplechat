package com.wakoo.simplechat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public final class LeaveProcessor extends MessageProcessor {
    public final String marker = "<<<";

    public LeaveProcessor(InetSocketAddress addr, byte[] okey, byte[] sign, ByteBuffer remain, ByteBuffer check_it) {
        super(addr, okey, sign, remain, check_it);
        ChatBox.SINGLETON.AddMessage("Из чата вышел " + nickname, this);
    }
}
