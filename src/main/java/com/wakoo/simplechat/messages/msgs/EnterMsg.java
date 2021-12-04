package com.wakoo.simplechat.messages.msgs;

import com.wakoo.simplechat.ProfileCatalog;
import com.wakoo.simplechat.messages.Message;

public interface EnterMsg extends Message {
    default String getMarker() {
        return ">>>";
    }
    default String getVisibleText() {
        return "В чат зашёл " + ProfileCatalog.SINGLETON.getNickname();
    }
}
