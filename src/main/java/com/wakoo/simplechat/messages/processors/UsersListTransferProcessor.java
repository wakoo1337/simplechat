package com.wakoo.simplechat.messages.processors;

import com.wakoo.simplechat.gui.UsersBox;
import com.wakoo.simplechat.messages.Message;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class UsersListTransferProcessor extends MessageProcessor implements Message {
    public UsersListTransferProcessor(InetSocketAddress party, final byte[] okey_arr, final byte[] sign_arr, ByteBuffer remain_in) {
        super(party, okey_arr, sign_arr, remain_in);
        int c = getInt();
        for (int i=0;i < c;i++) UsersBox.SINGLETON.addUser(getString());
    }

    public String getMarker() {
        return "";
    }

    public String getVisibleText() {
        return "";
    }
}
