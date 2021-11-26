package com.wakoo.simplechat.networking;

import com.wakoo.simplechat.ProfileCatalog;
import com.wakoo.simplechat.displays.ErrorDisplay;
import com.wakoo.simplechat.displays.MsgDisplay;
import com.wakoo.simplechat.gui.ConnectDisconnectItems;
import com.wakoo.simplechat.messages.Message;
import com.wakoo.simplechat.messages.generators.EnterGenerator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public final class NetworkingProcessor implements Runnable {
    public static final NetworkingProcessor SINGLETON = new NetworkingProcessor();
    private static Selector conn_sel;

    private static final Object sel_sync = new Object();

    private NetworkingProcessor() {
        try {
            conn_sel = Selector.open();
        } catch (IOException ioexcp) {
            err_disp.displayMessage(ioexcp, "Невозможно создать селектор");
        }
        (new Thread(this, "Поток обработки сети")).start();
    }

    @Override
    public void run() {
        try (ServerSocketChannel conn_listener = ServerSocketChannel.open()) {
            conn_listener.socket().bind(new InetSocketAddress(ProfileCatalog.SINGLETON.getListenPort()));
            conn_listener.socket().setReuseAddress(true);
            conn_listener.configureBlocking(false);
            SelectionKey conn_key = conn_listener.register(conn_sel, SelectionKey.OP_ACCEPT);
            while (!stop) {
                conn_sel.select();
                synchronized (sel_sync) {
                    Set<SelectionKey> selected = conn_sel.selectedKeys();
                    for (SelectionKey key : selected) {
                        if (key.equals(conn_key)) {
                            SocketChannel sock = conn_listener.accept();
                            if (sock != null) {
                                sock.configureBlocking(false);
                                SelectionKey new_key;
                                new_key = sock.register(conn_sel, SelectionKey.OP_READ);
                                new_key.attach(new ClientConnection(new_key));
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
            }
            for (SelectionKey key : conn_sel.keys()) {
                key.channel().close();
            }
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
                synchronized (sel_sync) {
                    srv_key = srv_conn.register(conn_sel, SelectionKey.OP_READ);
                    cl_conn = new ClientConnection(srv_key);
                    srv_key.attach(cl_conn);
                    conn_sel.wakeup();
                }
                connected = true;
                ConnectDisconnectItems.SINGLETON.lockConnectDisconnect(true);
                cl_conn.queueMsgSend(new EnterGenerator());
            }
        }

        public void disconnectServer() throws IOException {
            if (connected) {
                synchronized (sel_sync) {
                    srv_key.cancel();
                    srv_conn.close();
                    conn_sel.wakeup();
                }
                srv_conn = null;
                srv_key = null;
                connected = false;
                ConnectDisconnectItems.SINGLETON.lockConnectDisconnect(false);
            }
        }

        public boolean getConnected() {
            return connected;
        }

        private final MsgDisplay err_disp = new ErrorDisplay("Ошибка сети", "Ошибка при соединении с вышестоящим пользователем");
    }

    public void sendToAll(Message message) {
        sendToAllButServer(message);
        if (ServerConnection.SINGLETON.cl_conn != null) ServerConnection.SINGLETON.cl_conn.queueMsgSend(message);
    }

    public void sendToAllButServer(Message message) {
        for (SelectionKey key : conn_sel.keys()) {
            if ((!key.equals(ServerConnection.SINGLETON.srv_key)) && (key.attachment() != null))
                ((ClientConnection) key.attachment()).queueMsgSend(message);
        }
        conn_sel.wakeup();
    }

    boolean stop = false;

    public void stopIt() {
        stop = true;
        conn_sel.wakeup();
    }
}
