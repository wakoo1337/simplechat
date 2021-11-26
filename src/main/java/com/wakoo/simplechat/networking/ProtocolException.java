package com.wakoo.simplechat.networking;

public final class ProtocolException extends Exception {
    ProtocolException(String msg) {
        super(msg);
    }

    ProtocolException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
