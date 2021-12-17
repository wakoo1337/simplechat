package com.wakoo.simplechat.messages.processors;

import com.wakoo.simplechat.gui.ChatBox;
import com.wakoo.simplechat.gui.UsersBox;
import com.wakoo.simplechat.messages.msgs.LeaveMsg;
import com.wakoo.simplechat.networking.NetworkingProcessor;
import com.wakoo.simplechat.networking.ProtocolException;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public final class LeaveProcessor extends MessageProcessor implements LeaveMsg {
    public LeaveProcessor(InetSocketAddress addr, byte[] okey, byte[] sign, ByteBuffer remain) throws ProtocolException {
        super(addr, okey, sign, remain);
        UsersBox.SINGLETON.delUser(getNickname());
        ChatBox.SINGLETON.addMessage(this);
        NetworkingProcessor.SINGLETON.relayMessage(this, addr);
    }

    public String getVisibleText() {
        return getSignOk() + "Из чата вышел " + getNickname();
    }
}
