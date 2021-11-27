package com.wakoo.simplechat.networking;

import com.wakoo.simplechat.displays.ErrorDisplay;
import com.wakoo.simplechat.displays.MsgDisplay;
import com.wakoo.simplechat.messages.Message;
import com.wakoo.simplechat.messages.processors.MessageProcessor;
import com.wakoo.simplechat.messages.MessageTypes;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ClientConnection implements AutoCloseable {
    private ByteBuffer in_hdr;
    private ByteBuffer in_msg;
    private final SocketChannel channel;
    private final SelectionKey key;
    private final ArrayList<ByteBuffer> sendqueue = new ArrayList<>();

    public ClientConnection(SelectionKey sk) {
        channel = (SocketChannel) sk.channel();
        key = sk;
        createInHdr();
    }

    public void processInData() throws IOException, ProtocolException, ReflectiveOperationException {
        // Тут надо действовать в зависимости от того, существует ли тело сообщения, или нет
        if (in_msg == null) readHeader();
        else readMessage();
    }

    private void readHeader() throws IOException,ProtocolException {
        channel.read(in_hdr);
        if (!in_hdr.hasRemaining()) {
            in_hdr.flip();
            final int msg_magic = in_hdr.getInt();
            if (msg_magic != MessageTypes.magic) throw new ProtocolException("Неверное магическое число");
            final int msg_length = in_hdr.getInt();
            if (msg_length <= 0) throw new ProtocolException("Длина сообщения меньше или равна нулю");
            createInMsg(msg_length);
        }
    }

    private void readMessage() throws IOException,ProtocolException,ReflectiveOperationException {
        channel.read(in_msg);
        if (!in_msg.hasRemaining()) {
            in_msg.flip();
            try {
                byte[] sign = new byte[256];
                in_msg.get(sign);
                ByteBuffer check_it = in_msg.asReadOnlyBuffer();
                byte[] okey = new byte[294];
                in_msg.get(okey);
                final int type = in_msg.getInt();
                Class<MessageProcessor> msgproc = types.get(type);
                if (msgproc != null) {
                    Constructor<MessageProcessor> c = msgproc.getConstructor(InetSocketAddress.class, byte[].class, byte[].class, ByteBuffer.class, ByteBuffer.class);
                    c.newInstance(channel.getRemoteAddress(), okey, sign, in_msg, check_it);
                }
            } catch (BufferUnderflowException buexcp) {
                throw new ProtocolException("В сообщении отсутствуют цифровая подпись, открытый ключ, и/или длина", buexcp);
            } finally {
                in_msg = null;
                in_hdr.clear();
            }
        }
    }

    private void createInHdr() {
        in_hdr = ByteBuffer.allocate(8);
        in_hdr.order(ByteOrder.LITTLE_ENDIAN);
    }

    private void createInMsg(final int length) {
        in_msg = ByteBuffer.allocate(length);
        in_msg.order(ByteOrder.LITTLE_ENDIAN);
    }

    public void writeOutData() throws IOException {
        channel.write(sendqueue.toArray(new ByteBuffer[0]));
        while ((sendqueue.size() > 0) && (!sendqueue.get(0).hasRemaining())) sendqueue.remove(0);
        if (sendqueue.size() == 0) key.interestOpsAnd(SelectionKey.OP_READ);
    }

    public void queueDataWrite(final List<ByteBuffer> data) {
        synchronized (this) {
            sendqueue.addAll(data);
        }
        key.interestOpsOr(SelectionKey.OP_WRITE);
        key.selector().wakeup();
    }

    @Override
    public void close() throws IOException {
        if (key.isValid()) key.cancel();
        channel.close();
    }

    public void queueMsgSend(Message message) {
        queueDataWrite(message.export());
    }

    private static final HashMap<Integer, Class<MessageProcessor>> types;

    static {
        types = new HashMap<>();
        try {
            types.put(MessageTypes.MessageNoop, (Class<MessageProcessor>) Class.forName("com.wakoo.simplechat.messages.processors.NoopProcessor"));
            types.put(MessageTypes.MessageEnter, (Class<MessageProcessor>) Class.forName("com.wakoo.simplechat.messages.processors.EnterProcessor"));
            types.put(MessageTypes.MessageLeave, (Class<MessageProcessor>) Class.forName("com.wakoo.simplechat.messages.processors.LeaveProcessor"));
            types.put(MessageTypes.MessageText, (Class<MessageProcessor>) Class.forName("com.wakoo.simplechat.messages.processors.TextProcessor"));
            types.put(MessageTypes.MessageNicknameChange, null);
        } catch (ClassNotFoundException noclassexcp) {
            MsgDisplay err_disp = new ErrorDisplay("Ошибка рефлексии");
            err_disp.displayMessage(noclassexcp, "Невозможно получить доступ к классам-обработчикам сообщений");
        }
    }

    public boolean isSendQueueEmpty() {
        return sendqueue.isEmpty();
    }
}
