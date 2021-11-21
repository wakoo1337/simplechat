package com.wakoo.simplechat;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public final class NoopProcessor extends MessageProcessor {
    String marker = "";
    NoopProcessor(InetSocketAddress addr, byte[] okey, byte[] sign, ByteBuffer remain) {
        super(addr, okey, sign, remain);
    }
}
