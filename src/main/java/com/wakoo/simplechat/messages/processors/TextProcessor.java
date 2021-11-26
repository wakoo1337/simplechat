package com.wakoo.simplechat.messages.processors;

import com.wakoo.simplechat.gui.ChatBox;
import com.wakoo.simplechat.messages.TextMessage;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class TextProcessor extends MessageProcessor implements TextMessage {
    private final String text;

    public TextProcessor(InetSocketAddress party, final byte[] okey_arr, final byte[] sign_arr, ByteBuffer remain_in, ByteBuffer check_it) {
        super(party, okey_arr, sign_arr, remain_in, check_it);
        text = getString();
        ChatBox.SINGLETON.addMessage("<" + nickname + "> " + text, this);
    }

    public String getMarker() {
        return "   ";
    }

    public String getText() {
        return text;
    }
}
