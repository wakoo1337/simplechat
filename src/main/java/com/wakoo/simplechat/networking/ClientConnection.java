package com.wakoo.simplechat.networking;

import com.wakoo.simplechat.gui.displays.ErrorDisplay;
import com.wakoo.simplechat.gui.displays.MsgDisplay;
import com.wakoo.simplechat.messages.Message;
import com.wakoo.simplechat.messages.MessageTypes;
import com.wakoo.simplechat.messages.processors.MessageProcessor;

import java.io.IOException;
import java.lang.reflect.Constructor;
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
    private final ByteBuffer in_hdr;
    private ByteBuffer in_msg;
    private final SocketChannel channel;
    private final SelectionKey key;
    private final ArrayList<ByteBuffer> sendqueue = new ArrayList<>();
    private final static int hdr_len = 8;
    private boolean close_me = false;

    public ClientConnection(SelectionKey sk) {
        channel = (SocketChannel) sk.channel();
        key = sk;
        in_hdr = allocLEBuffer(hdr_len);
    }

    public void processInData() throws IOException, ProtocolException, ReflectiveOperationException {
        // Тут надо действовать в зависимости от того, существует ли тело сообщения, или нет
        if (in_msg == null) readHeader();
        else readMessage();
    }

    private void readHeader() throws IOException, ProtocolException, ReflectiveOperationException {
        channel.read(in_hdr);
        if (!in_hdr.hasRemaining()) {
            in_hdr.flip();
            final int msg_magic = in_hdr.getInt();
            if (msg_magic != MessageTypes.magic) throw new ProtocolException("Неверное магическое число");
            final int msg_length = in_hdr.getInt();
            if (msg_length <= 0) throw new ProtocolException("Длина сообщения меньше или равна нулю");
            in_msg = allocLEBuffer(msg_length);
            readMessage();
        }
    }

    private void readMessage() throws IOException, ProtocolException, ReflectiveOperationException {
        channel.read(in_msg);
        if (!in_msg.hasRemaining()) {
            in_msg.flip();
            try {
                final int sign_len = in_msg.getInt();
                byte[] sign = new byte[sign_len];
                in_msg.get(sign);
                in_msg.mark();
                final int okey_len = in_msg.getInt();
                byte[] okey = new byte[okey_len];
                in_msg.get(okey);
                final int type = in_msg.getInt();
                in_msg.reset();
                Constructor<MessageProcessor> msgproc = types.get(type);
                if (msgproc != null) {
                    msgproc.newInstance(channel.getRemoteAddress(), okey, sign, in_msg);
                } else throw new ProtocolException("Получено сообщение с неверным типом");
            } catch (BufferUnderflowException buexcp) {
                throw new ProtocolException("В сообщении отсутствуют цифровая подпись, открытый ключ, и/или длина", buexcp);
            } finally {
                in_msg = null;
                in_hdr.clear();
            }
        }
    }

    private ByteBuffer allocLEBuffer(final int length) {
        ByteBuffer bb = ByteBuffer.allocate(length);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb;
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

    private static final HashMap<Integer, Constructor<MessageProcessor>> types;

    static {
        types = new HashMap<>();
        try {
            types.put(MessageTypes.MessageNoop, (Constructor<MessageProcessor>) Class.forName("com.wakoo.simplechat.messages.processors.NoopProcessor").getConstructor(InetSocketAddress.class, byte[].class, byte[].class, ByteBuffer.class));
            types.put(MessageTypes.MessageEnter, (Constructor<MessageProcessor>) Class.forName("com.wakoo.simplechat.messages.processors.EnterProcessor").getConstructor(InetSocketAddress.class, byte[].class, byte[].class, ByteBuffer.class));
            types.put(MessageTypes.MessageLeave, (Constructor<MessageProcessor>) Class.forName("com.wakoo.simplechat.messages.processors.LeaveProcessor").getConstructor(InetSocketAddress.class, byte[].class, byte[].class, ByteBuffer.class));
            types.put(MessageTypes.MessageText, (Constructor<MessageProcessor>) Class.forName("com.wakoo.simplechat.messages.processors.TextProcessor").getConstructor(InetSocketAddress.class, byte[].class, byte[].class, ByteBuffer.class));
            types.put(MessageTypes.MessageUsersListTransfer, (Constructor<MessageProcessor>) Class.forName("com.wakoo.simplechat.messages.processors.UsersListTransferProcessor").getConstructor(InetSocketAddress.class, byte[].class, byte[].class, ByteBuffer.class));
        } catch (ReflectiveOperationException refexcp) {
            MsgDisplay err_disp = new ErrorDisplay("Ошибка рефлексии");
            err_disp.displayMessage(refexcp, "Невозможно получить доступ к классам-обработчикам сообщений");
        }
    }

    public boolean isSendQueueEmpty() {
        return sendqueue.isEmpty();
    }

    public InetSocketAddress getRemoteAddress() {
        try {
            return (InetSocketAddress) channel.getRemoteAddress();
        } catch (IOException ioexcp) {
            MsgDisplay err_disp = new ErrorDisplay("Ошибка ввода/вывода");
            err_disp.displayMessage(ioexcp, "Невозможно получить удалённый адрес");
            return null;
        }
    }

    public boolean getCloseMe() {
        return close_me;
    }

    public void markToClose() {
        close_me = true;
        key.selector().wakeup();
    }
}
