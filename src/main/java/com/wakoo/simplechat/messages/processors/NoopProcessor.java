package com.wakoo.simplechat.messages.processors;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public final class NoopProcessor extends MessageProcessor {
    String marker = "";

    public NoopProcessor(InetSocketAddress addr, byte[] okey, byte[] sign, ByteBuffer remain) {
        super(addr, okey, sign, remain);
    }

    @Override
    public String getMarker() {
        return "";
    }

    public String getVisibleText() {
        return "";
    }
}
