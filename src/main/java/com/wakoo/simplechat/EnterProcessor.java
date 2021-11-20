package com.wakoo.simplechat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public final class EnterProcessor extends MessageProcessor {
    public final String marker = ">>>";

    EnterProcessor(final int type, final ByteBuffer msg) {
        super(type, msg);
        ChatBox.SINGLETON.AddMessage("В чат зашёл " + nickname, this);
    }
}
