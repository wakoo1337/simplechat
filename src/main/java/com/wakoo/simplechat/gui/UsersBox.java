package com.wakoo.simplechat.gui;

import javafx.application.Platform;
import javafx.scene.control.ListView;

import java.util.List;

public final class UsersBox {
    public static final UsersBox SINGLETON = new UsersBox();
    private ListView<String> lv;

    public void setListView(ListView<String> lv) {
        this.lv = lv;
    }

    public List<String> getUsersList() {
        return lv.getItems();
    }

    public void addUser(String user) {
        Platform.runLater(() -> lv.getItems().add(user));
    }

    public void delUser(String user) {
        Platform.runLater(() -> lv.getItems().remove(user));
    }
}
