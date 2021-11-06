package com.wakoo.simplechat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;

public final class MessageDispatcher implements Runnable {
    private final ByteBuffer msg;
    private final InetSocketAddress party_addr;
    private static final HashMap<Integer, MessageProcessor> types;
    static {
        types = new HashMap<>();
        types.put(MessageTypes.MessageNoop, NoopProcessor.SINGLETON);
        types.put(MessageTypes.MessageEnter, EnterProcessor.SINGLETON);
        types.put(MessageTypes.MessageLeave, null);
        types.put(MessageTypes.MessageText, null);
        types.put(MessageTypes.MessageNicknameChange, null);
    }
    public MessageDispatcher(final ByteBuffer msg, final InetSocketAddress addr) {
        this.msg = msg;
        this.party_addr = addr;
        (new Thread(this)).start();
    }
    @Override
    public void run() {
        final int type;
        type = msg.getInt();
        types.get(type);
    }

    public static final class MessageTypes {
        public static final int MessageNoop = 0;
        public static final int MessageEnter = 1;
        public static final int MessageLeave = 2;
        public static final int MessageText = 3;
        public static final int MessageNicknameChange = 4;
    }
}
