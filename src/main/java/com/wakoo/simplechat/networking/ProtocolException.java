package com.wakoo.simplechat.networking;

public final class ProtocolException extends Exception {
    public ProtocolException(String msg) {
        super(msg);
    }

    public ProtocolException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
