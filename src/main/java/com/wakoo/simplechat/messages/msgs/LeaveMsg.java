package com.wakoo.simplechat.messages.msgs;

import com.wakoo.simplechat.ProfileCatalog;
import com.wakoo.simplechat.messages.Message;

public interface LeaveMsg extends Message {
    default String getMarker() {
        return "<<<";
    }
    default String getVisibleText() {
        return "Из чата вышел " + ProfileCatalog.SINGLETON.getNickname();
    }
}
