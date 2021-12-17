package com.wakoo.simplechat.messages.generators;

import com.wakoo.simplechat.messages.Message;

import java.nio.ByteBuffer;
import java.util.List;

public class InfoGenerator implements Message {
    public InfoGenerator(String msg) {
        this.msg = msg;
    }

    private final String msg;

    public List<ByteBuffer> export() {
        return null;
    }

    public String getMarker() {
        return "!!!";
    }

    public String getSignOk() {
        return "";
    }

    public String getNickname() {
        return "SimpleChat";
    }

    public String getVisibleText() {
        return msg;
    }
}
