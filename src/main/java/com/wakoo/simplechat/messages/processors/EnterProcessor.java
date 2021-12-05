package com.wakoo.simplechat.messages.processors;

import com.wakoo.simplechat.ProfileCatalog;
import com.wakoo.simplechat.gui.UsersBox;
import com.wakoo.simplechat.messages.msgs.EnterMsg;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public final class EnterProcessor extends MessageProcessor implements EnterMsg {
    public EnterProcessor(InetSocketAddress addr, byte[] okey, byte[] sign, ByteBuffer remain) {
        super(addr, okey, sign, remain);
        UsersBox.SINGLETON.addUser(getNickname());
    }

    public String getVisibleText() {
        return getSignOk() + "В чат зашёл " + getNickname();
    }
}
