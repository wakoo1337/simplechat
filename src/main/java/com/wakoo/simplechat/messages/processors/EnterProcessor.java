package com.wakoo.simplechat.messages.processors;

import com.wakoo.simplechat.gui.ChatBox;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public final class EnterProcessor extends MessageProcessor {
    public String getMarker() {
        return ">>>";
    }

    public EnterProcessor(InetSocketAddress addr, byte[] okey, byte[] sign, ByteBuffer remain, ByteBuffer check_it) {
        super(addr, okey, sign, remain, check_it);
        ChatBox.SINGLETON.addMessage("В чат зашёл " + nickname, this);
    }
}
