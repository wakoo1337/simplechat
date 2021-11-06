package com.wakoo.simplechat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;

public final class NetworkingProcessor implements Runnable {
    public static final NetworkingProcessor SINGLETON = new NetworkingProcessor();
    private static Selector conn_sel;

    private NetworkingProcessor() {
        try {
            conn_sel = Selector.open();
        } catch (IOException ioexcp) {
            err_disp.DisplayMessage("Невозможно создать селектор");
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
            while (true) {
                conn_sel.select();
                synchronized (conn_sel) {
                    Set<SelectionKey> selected = conn_sel.selectedKeys();
                    for (SelectionKey key : selected) {
                        if (key.equals(conn_key)) {
                            SocketChannel sock = conn_listener.accept();
                            if (sock != null) {
                                sock.configureBlocking(false);
                                SelectionKey new_key = sock.register(conn_sel, SelectionKey.OP_READ);
                                new_key.attach(new ClientConnection(new_key));
                                ArrayList<ByteBuffer> arrl = new ArrayList<ByteBuffer>();
                                ByteBuffer bb = StandardCharsets.US_ASCII.encode("Usami Renko is so cute, I`d fuck her.");
                                arrl.add(bb);
                                ((ClientConnection) new_key.attachment()).QueueDataWrite(arrl);
                            }
                        } else {
                            try {
                                ClientConnection connection = (ClientConnection) key.attachment();
                                if (key.isReadable()) {
                                    connection.ProcessInData();
                                }
                                if (key.isWritable()) {
                                    connection.WriteOutData();
                                }
                                if (key.isConnectable()) {
                                    key.cancel();
                                    key.channel().close();
                                }
                            } catch (IOException ioexcp) {
                                key.cancel();
                                key.channel().close();
                                err_disp.DisplayMessage("Ошибка ввода-вывода на сокете");
                            } catch (ProtocolException protoexcp) {
                                key.cancel();
                                key.channel().close();
                                err_disp.DisplayMessage("Неверно сформированное сообщение на сокете");
                            }
                        }
                    }
                }
            }
        } catch (IOException ioexcp) {
            err_disp.DisplayMessage("Проблемы с ожиданием соединения");
        }
    }

    private final MsgDisplay err_disp = new ErrorDisplay("Ошибка сети", "Ошибка в потоке обработки сетевых соединений");

    public static final class ServerConnection {
        public static final ServerConnection SINGLETON = new ServerConnection();
        private boolean connected = false;
        private SocketChannel srv_conn;
        private SelectionKey srv_key;

        public void ConnectServer(final InetAddress address, final int port) {
            if (!connected) {
                try {
                    srv_conn = SocketChannel.open();
                    srv_conn.connect(new InetSocketAddress(address, port));
                    srv_conn.configureBlocking(false);
                    synchronized (conn_sel) {
                        srv_key = srv_conn.register(conn_sel, SelectionKey.OP_READ);
                        srv_key.attach(new ClientConnection(srv_key));
                        conn_sel.wakeup();
                    }
                    connected = true;
                } catch (IOException ioexcp) {
                    err_disp.DisplayMessage("Невозможно соединиться с вышестоящим сервером");
                }
            }
        }

        public void DisconnectServer() {
            if (connected) {
                try {
                    synchronized (conn_sel) {
                        srv_key.cancel();
                        srv_conn.close();
                        conn_sel.wakeup();
                    }
                    connected = false;
                    srv_conn = null;
                    srv_key = null;
                } catch (IOException ioexcp) {
                    err_disp.DisplayMessage("Невозможно закрыть соединение");
                }
            }
        }

        public boolean getConnected() {
            return connected;
        }

        private final MsgDisplay err_disp = new ErrorDisplay("Ошибка сети", "Ошибка при соединении с вышестоящим пользователем");
    }


}
