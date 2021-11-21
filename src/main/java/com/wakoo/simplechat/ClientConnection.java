package com.wakoo.simplechat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public final class ClientConnection implements AutoCloseable {
    private ByteBuffer in_hdr;
    private ByteBuffer in_msg;
    private final SocketChannel channel;
    private final SelectionKey key;
    private final ArrayList<ByteBuffer> sendqueue = new ArrayList<>();
    public ClientConnection(SelectionKey sk) {
        channel = (SocketChannel) sk.channel();
        key = sk;
        CreateInHdr();
    }
    public void ProcessInData() throws IOException, ProtocolException {
        if (in_hdr.hasRemaining()) {
            channel.read(in_hdr);
            if (!in_hdr.hasRemaining()) {
                in_hdr.flip();
                final int msg_magic = in_hdr.getInt();
                final int msg_length = in_hdr.getInt();
                if ((msg_magic != MessageDispatcher.MessageTypes.magic) || (msg_length <= 0)) throw new ProtocolException();
                CreateInMsg(msg_length);
            }
        } else {
            channel.read(in_msg);
            if (!in_msg.hasRemaining()) {
                in_msg.flip();
                new MessageDispatcher(in_msg, (InetSocketAddress) channel.getRemoteAddress());
                in_msg = null;
                in_hdr.clear();
            }
        }
    }
    private void CreateInHdr() {
        in_hdr = ByteBuffer.allocate(8);
        in_hdr.order(ByteOrder.LITTLE_ENDIAN);
    }
    private void CreateInMsg(final int length) {
        in_msg = ByteBuffer.allocate(length);
        in_msg.order(ByteOrder.LITTLE_ENDIAN);
    }
    public void WriteOutData() throws IOException {
        channel.write(sendqueue.toArray(new ByteBuffer[0]));
        while ((sendqueue.size() > 0) && (!sendqueue.get(0).hasRemaining())) sendqueue.remove(0);
        if (sendqueue.size() == 0) key.interestOpsAnd(SelectionKey.OP_READ);
    }
    public void QueueDataWrite(final ArrayList<ByteBuffer> data) {
        synchronized (key) {
            sendqueue.addAll(data);
        }
        key.interestOpsOr(SelectionKey.OP_WRITE);
    }

    @Override
    public void close() throws Exception {
        if (key.isValid()) key.cancel();
        channel.close();
    }

    public void QueueMsgSend(Exportable exportable) {
        QueueDataWrite(exportable.export());
    }
}
