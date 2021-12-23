package com.wakoo.simplechat.messages.processors;

import com.wakoo.simplechat.gui.UsersBox;
import com.wakoo.simplechat.messages.Message;
import com.wakoo.simplechat.networking.ProtocolException;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public final class UsersListTransferProcessor extends MessageProcessor implements Message {
    public UsersListTransferProcessor(InetSocketAddress party, final byte[] okey_arr, final byte[] sign_arr, ByteBuffer remain_in) throws ProtocolException {
        super(party, okey_arr, sign_arr, remain_in);
        int c = getInt();
        if (c <= 0) throw new ProtocolException("Число пользователей в списке не может быть отрицательным или нулевым");
        for (int i = 0; i < c; i++) UsersBox.SINGLETON.addUser(getString());
    }

    public String getMarker() {
        return "";
    }

    public String getVisibleText() {
        return "";
    }
}
