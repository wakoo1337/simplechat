package com.wakoo.simplechat.messages.processors;

import com.wakoo.simplechat.messages.msgs.TextMsg;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class TextProcessor extends MessageProcessor implements TextMsg {
    private final String text;

    public TextProcessor(InetSocketAddress party, final byte[] okey_arr, final byte[] sign_arr, ByteBuffer remain_in) {
        super(party, okey_arr, sign_arr, remain_in);
        text = getString();
    }

    public String getValueText() {
        return text;
    }
}
