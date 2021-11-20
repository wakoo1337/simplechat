package com.wakoo.simplechat;


import java.nio.ByteBuffer;

public final class NoopProcessor extends MessageProcessor {
    String marker = "";
    NoopProcessor(int type, ByteBuffer msg) {
        super(type, msg);
    }
}
