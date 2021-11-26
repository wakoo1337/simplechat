package com.wakoo.simplechat.messages.processors;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public final class NoopProcessor extends MessageProcessor {
    String marker = "";
    public NoopProcessor(InetSocketAddress addr, byte[] okey, byte[] sign, ByteBuffer remain, ByteBuffer check_it) {
        super(addr, okey, sign, remain, check_it);
    }

    @Override
    public String getMarker() {
        return "";
    }
}
