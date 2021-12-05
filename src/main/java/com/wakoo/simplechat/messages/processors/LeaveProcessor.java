package com.wakoo.simplechat.messages.processors;

import com.wakoo.simplechat.ProfileCatalog;
import com.wakoo.simplechat.gui.UsersBox;
import com.wakoo.simplechat.messages.msgs.LeaveMsg;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public final class LeaveProcessor extends MessageProcessor implements LeaveMsg {
    public LeaveProcessor(InetSocketAddress addr, byte[] okey, byte[] sign, ByteBuffer remain) {
        super(addr, okey, sign, remain);
        UsersBox.SINGLETON.delUser(getNickname());
    }

    public String getVisibleText() {
        return getSignOk() + "Из чата вышел " + getNickname();
    }
}
