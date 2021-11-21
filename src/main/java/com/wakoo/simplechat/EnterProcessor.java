package com.wakoo.simplechat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public final class EnterProcessor extends MessageProcessor {
    public final String marker = ">>>";

    EnterProcessor(InetSocketAddress addr, byte[] okey, byte[] sign, ByteBuffer remain) {
        super(addr, okey, sign, remain);
        ChatBox.SINGLETON.AddMessage("В чат зашёл " + nickname, this);
    }
}
