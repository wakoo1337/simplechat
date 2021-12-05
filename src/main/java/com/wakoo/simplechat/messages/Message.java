package com.wakoo.simplechat.messages;

import java.nio.ByteBuffer;
import java.util.List;

public interface Message {
    List<ByteBuffer> export();

    String getMarker();

    String getSignOk();
    String getNickname();

    String getVisibleText();
}
