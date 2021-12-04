package com.wakoo.simplechat.messages.generators;

import com.wakoo.simplechat.messages.Message;

import java.nio.ByteBuffer;
import java.util.List;

public class InfoGenerator implements Message {
    public InfoGenerator(String msg) {
        this.msg = msg;
    }

    private String msg;

    public List<ByteBuffer> export() {
        return null;
    }

    public String getMarker() {
        return "!!!";
    }

    public boolean getSignOk() {
        return true;
    }

    public String getNickname() {return "SimpleChat";}

    public String getVisibleText() {
        return msg;
    }
}
