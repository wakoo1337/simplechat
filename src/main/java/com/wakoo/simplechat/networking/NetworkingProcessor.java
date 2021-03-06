package com.wakoo.simplechat.networking;

import com.wakoo.simplechat.ProfileCatalog;
import com.wakoo.simplechat.gui.ChatBox;
import com.wakoo.simplechat.gui.ConnectDisconnectItems;
import com.wakoo.simplechat.gui.UsersBox;
import com.wakoo.simplechat.gui.displays.ErrorDisplay;
import com.wakoo.simplechat.gui.displays.MsgDisplay;
import com.wakoo.simplechat.messages.Message;
import com.wakoo.simplechat.messages.generators.EnterGenerator;
import com.wakoo.simplechat.messages.generators.InfoGenerator;
import com.wakoo.simplechat.messages.generators.LeaveGenerator;
import com.wakoo.simplechat.messages.generators.UsersListTransferGenerator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.function.Predicate;

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
                                        if (connection.getCloseMe() && connection.isSendQueueEmpty()) {
                                            connection.close();
                                        }
                                    }
                                    areSendQueuesEmpty &= connection.isSendQueueEmpty();
                                } else {
                                    disconnectClientOrServer(key);
                                }
                            } catch (IOException ioexcp) {
                                disconnectClientOrServer(key);
                                err_disp.displayMessage(ioexcp, "Ошибка ввода-вывода на сокете");
                            } catch (ProtocolException protoexcp) {
                                disconnectClientOrServer(key);
                                err_disp.displayMessage(protoexcp, "Неверно сформированное сообщение на сокете");
                            } catch (ReflectiveOperationException reflopexcp) {
                                if (reflopexcp instanceof InvocationTargetException) {
                                    Throwable previous = reflopexcp.getCause();
                                    err_disp.displayMessage(previous, "Ошибка при обработке сообщения");
                                    if ((previous instanceof ProtocolException))
                                        disconnectClientOrServer(key);
                                } else
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
            err_disp.displayMessage(ioexcp, "Проблемы с ожиданием или закрытием соединения");
        }
    }

    private final MsgDisplay err_disp = new ErrorDisplay("Ошибка сети", "Ошибка в потоке обработки сетевых соединений");

    private void disconnectClientOrServer(SelectionKey key) throws IOException {
        if (ServerConnection.SINGLETON.isConnected() && ServerConnection.SINGLETON.srv_key.equals(key)) {
            ServerConnection.SINGLETON.setConnected(false);
            ChatBox.SINGLETON.addMessage(new InfoGenerator("Отключено от сервера"));
        }
        key.cancel();
        ((SocketChannel) key.channel()).close();
    }

    public static final class ServerConnection {
        public static final ServerConnection SINGLETON = new ServerConnection();
        private boolean connected = false;
        public SocketChannel srv_conn;
        public SelectionKey srv_key;
        public ClientConnection cl_conn;

        public void connectServer(final InetAddress address, final int port) throws IOException {
            if (!connected) {
                srv_conn = SocketChannel.open();
                InetSocketAddress isa = new InetSocketAddress(address, port);
                srv_conn.connect(isa);
                srv_conn.configureBlocking(false);
                synchronized (this) {
                    srv_key = srv_conn.register(conn_sel, SelectionKey.OP_READ);
                    cl_conn = new ClientConnection(srv_key);
                    srv_key.attach(cl_conn);
                }
                cl_conn.queueMsgSend(new EnterGenerator());
                setConnected(true);
                ChatBox.SINGLETON.addMessage(new InfoGenerator("Подключено к " + isa));
            }
        }

        public void disconnectServer() throws IOException {
            if (connected) {
                cl_conn.markToClose();
                srv_conn = null;
                srv_key = null;
                cl_conn = null;
                setConnected(false);
                ChatBox.SINGLETON.addMessage(new InfoGenerator("Отключено от сервера"));
                UsersBox.SINGLETON.clear();
            }
        }

        public boolean isConnected() {
            return connected;
        }

        public void setConnected(boolean value) {
            connected = value;
            ConnectDisconnectItems.SINGLETON.lockConnectDisconnect();
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

    private void sendConditional(Message msg, Predicate<ClientConnection> condition, boolean server) {
        for (SelectionKey key : conn_sel.keys()) {
            ClientConnection ck = (ClientConnection) key.attachment();
            if ((ck != null) && (server || (!key.equals(ServerConnection.SINGLETON.srv_key))) && condition.test(ck)) {
                ck.queueMsgSend(msg);
            }
        }
    }

    public void sendTo(Message msg, boolean server) {
        sendConditional(msg, (ClientConnection conn) -> true, server);
    }

    public void relayMessage(Message msg, InetSocketAddress from) {
        sendConditional(msg, (ClientConnection conn) -> (!from.equals(conn.getRemoteAddress())), true);
    }
}
