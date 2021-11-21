package com.wakoo.simplechat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class TextProcessor extends MessageProcessor {
    public TextProcessor(InetSocketAddress party, final byte[] okey_arr, final byte[] sign_arr, ByteBuffer remain_in, ByteBuffer check_it) {
        super(party, okey_arr, sign_arr, remain_in, check_it);
        String val = GetString();
        ChatBox.SINGLETON.AddMessage("<" + nickname + "> " + val, this);
    }
}
