package com.wakoo.simplechat.networking;

import com.wakoo.simplechat.ProfileCatalog;
import com.wakoo.simplechat.displays.ErrorDisplay;
import com.wakoo.simplechat.displays.MsgDisplay;
import com.wakoo.simplechat.gui.ConnectDisconnectItems;
import com.wakoo.simplechat.messages.Message;
import com.wakoo.simplechat.messages.generators.EnterGenerator;
import com.wakoo.simplechat.messages.generators.LeaveGenerator;
import com.wakoo.simplechat.messages.generators.UsersListTransferGenerator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.function.Consumer;

public final class NetworkingProcessor implements Runnable {
    public static final NetworkingProcessor SINGLETON = new NetworkingProcessor();
    private static Selector conn_sel;
    private static Thread thread;

    private NetworkingProcessor() {
        try {
            conn_sel = Selector.open();
        } catch (IOException ioexcp) {
            err_disp.displayMessage(ioexcp, "Невозможно создать селектор");
        }
        thread = new Thread(this, "Поток обработки сети");
        thread.start();
    }

    @Override
    public void run() {
        try (ServerSocketChannel conn_listener = ServerSocketChannel.open()) {
            conn_listener.socket().bind(new InetSocketAddress(ProfileCatalog.SINGLETON.getListenPort()));
            conn_listener.socket().setReuseAddress(true);
            conn_listener.configureBlocking(false);
            SelectionKey conn_key = conn_listener.register(conn_sel, SelectionKey.OP_ACCEPT);
            while (true) {
                conn_sel.select();
                boolean areSendQueuesEmpty = true;
                synchronized (this) {
                    Set<SelectionKey> selected = conn_sel.selectedKeys();
                    for (SelectionKey key : selected) {
                        if (key.equals(conn_key)) {
                            SocketChannel sock = conn_listener.accept();
                            if (sock != null) {
                                sock.configureBlocking(false);
                                SelectionKey new_key;
                                new_key = sock.register(conn_sel, SelectionKey.OP_READ);
                                ClientConnection conn = new ClientConnection(new_key);
                                new_key.attach(conn);
                                conn.queueMsgSend(new UsersListTransferGenerator(true));
                            }
                        } else {
                            ClientConnection connection = (ClientConnection) key.attachment();
                            SocketChannel channel = (SocketChannel) key.channel();
                            try {
                                if (!channel.socket().isClosed()) {
                                    if (key.isReadable()) {
                                        connection.processInData();
                                    }
                                    if (key.isWritable()) {
                                        connection.writeOutData();
                                    }
                                    areSendQueuesEmpty &= connection.isSendQueueEmpty();
                                } else {
                                    key.cancel();
                                    key.channel().close();
                                    if (channel == ServerConnection.SINGLETON.srv_conn)
                                        ServerConnection.SINGLETON.connected = false;
                                }
                            } catch (IOException ioexcp) {
                                key.cancel();
                                channel.close();
                                err_disp.displayMessage(ioexcp, "Ошибка ввода-вывода на сокете");
                            } catch (ProtocolException protoexcp) {
                                key.cancel();
                                channel.close();
                                err_disp.displayMessage(protoexcp, "Неверно сформированное сообщение на сокете");
                            } catch (ReflectiveOperationException reflopexcp) {
                                err_disp.displayMessage(reflopexcp, "Невозможно создать экземпляр класса-обработчика сетевого сообщения");
                            }
                        }
                    }
                }
                if (stop_begin) {
                    if (conn_key.isValid()) conn_key.cancel();
                    if (areSendQueuesEmpty) break;
                }
            }
            // Соединения не закрываем, операционка сделает это за нас
        } catch (IOException ioexcp) {
            err_disp.displayMessage(ioexcp, "Проблемы с ожиданием соединения");
        }
    }

    private final MsgDisplay err_disp = new ErrorDisplay("Ошибка сети", "Ошибка в потоке обработки сетевых соединений");

    public static final class ServerConnection {
        public static final ServerConnection SINGLETON = new ServerConnection();
        private boolean connected = false;
        public SocketChannel srv_conn;
        public SelectionKey srv_key;
        public ClientConnection cl_conn;

        public void connectServer(final InetAddress address, final int port) throws IOException {
            if (!connected) {
                srv_conn = SocketChannel.open();
                srv_conn.connect(new InetSocketAddress(address, port));
                srv_conn.configureBlocking(false);
                synchronized (this) {
                    srv_key = srv_conn.register(conn_sel, SelectionKey.OP_READ);
                    cl_conn = new ClientConnection(srv_key);
                    srv_key.attach(cl_conn);
                }
                cl_conn.queueMsgSend(new EnterGenerator());
                connected = true;
                ConnectDisconnectItems.SINGLETON.lockConnectDisconnect();
            }
        }

        public void disconnectServer() throws IOException {
            if (connected) {
                synchronized (this) {
                    srv_key.cancel();
                    srv_conn.close();
                    conn_sel.wakeup();
                }
                srv_conn = null;
                srv_key = null;
                connected = false;
                ConnectDisconnectItems.SINGLETON.lockConnectDisconnect();
            }
        }

        public boolean isConnected() {
            return connected;
        }
    }

    boolean stop_begin = false;

    public void stopIt() {
        stop_begin = true;
        Message leave_notify = new LeaveGenerator();
        sendTo(leave_notify, true);
        conn_sel.wakeup();
        boolean joined = false;
        while (!joined) {
            try {
                thread.join();
                joined = true;
            } catch (InterruptedException ignored) {
                continue;
            }
        }
    }

    private void iterateConnections(Consumer<ClientConnection> consumer, boolean server) {
        for (SelectionKey key : conn_sel.keys()) {
            ClientConnection ck = (ClientConnection) key.attachment();
            if ((ck != null) && (server || (!key.equals(ServerConnection.SINGLETON.srv_key)))) {
                consumer.accept(ck);
            }
        }
    }

    public void sendTo(Message msg, boolean server) {
        iterateConnections((ClientConnection conn) -> {
            conn.queueMsgSend(msg);
        }, server);
    }

    public void relayMessage(Message msg, InetSocketAddress from) {
        iterateConnections((ClientConnection conn) -> {
            if (!from.equals(conn.getRemoteAddress())) conn.queueMsgSend(msg);
        }, true);
    }
}
