package com.wakoo.simplechat.gui;

import com.wakoo.simplechat.networking.NetworkingProcessor;
import javafx.scene.control.MenuItem;

public final class ConnectDisconnectItems {
    private MenuItem connectMenuItem;
    private MenuItem disconnectMenuItem;

    public static final ConnectDisconnectItems SINGLETON = new ConnectDisconnectItems();

    public void setConnectMenuItem(MenuItem item) {
        connectMenuItem = item;
    }

    public void setDisconnectMenuItem(MenuItem item) {
        disconnectMenuItem = item;
    }

    public MenuItem getConnectMenuItem() {
        return connectMenuItem;
    }

    public MenuItem getDisconnectMenuItem() {
        return disconnectMenuItem;
    }

    public void lockConnectDisconnect() {
        boolean status = NetworkingProcessor.ServerConnection.SINGLETON.isConnected();
        connectMenuItem.setDisable(status);
        disconnectMenuItem.setDisable(!status);
    }
}
