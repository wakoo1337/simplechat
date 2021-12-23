package com.wakoo.simplechat.messages.generators;

import com.wakoo.simplechat.ProfileCatalog;
import com.wakoo.simplechat.gui.UsersBox;
import com.wakoo.simplechat.messages.Message;
import com.wakoo.simplechat.messages.MessageTypes;

import java.util.List;

public final class UsersListTransferGenerator extends MessageGenerator implements Message {
    public UsersListTransferGenerator(boolean self) {
        super(MessageTypes.MessageUsersListTransfer);
        List<String> users;
        users = UsersBox.SINGLETON.getUsersList();
        insertInt(users.size() + (self ? 1 : 0), false);
        for (String user : users) insertString(user, false);
        if (self) insertString(ProfileCatalog.SINGLETON.getNickname(), false);
        finish();
    }

    @Override
    public String getMarker() {
        return "";
    }

    public String getVisibleText() {
        return "";
    }
}
