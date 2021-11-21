package com.wakoo.simplechat;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;

public final class MessageDispatcher {
    private static final HashMap<Integer, Class<?>> types;
    static {
        types = new HashMap<>();
        try {
            types.put(MessageTypes.MessageNoop, Class.forName("com.wakoo.simplechat.NoopProcessor"));
            types.put(MessageTypes.MessageEnter, Class.forName("com.wakoo.simplechat.EnterProcessor"));
            types.put(MessageTypes.MessageLeave, Class.forName("com.wakoo.simplechat.LeaveProcessor"));
            types.put(MessageTypes.MessageText, null);
            types.put(MessageTypes.MessageNicknameChange, null);
        } catch (ClassNotFoundException noclassexcp) {

        }
    }
    public MessageDispatcher(final ByteBuffer msg, final InetSocketAddress addr) {
        byte[] sign = new byte[256];
        msg.get(sign);
        byte[] okey = new byte[294];
        msg.get(okey);
        final int type = msg.getInt();
        Class<?> msgproc = types.get(type);
        if (msgproc != null) {
            try {
                msgproc.getConstructor(InetSocketAddress.class, byte[].class, byte[].class, ByteBuffer.class).newInstance(addr, sign, okey, msg);
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static final class MessageTypes {
        public static final int MessageNoop = 0;
        public static final int MessageEnter = 1;
        public static final int MessageLeave = 2;
        public static final int MessageText = 3;
        public static final int MessageNicknameChange = 4;

        public static final int magic = 0x20150829;
    }
}
