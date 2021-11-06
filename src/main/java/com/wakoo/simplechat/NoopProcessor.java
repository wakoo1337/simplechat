package com.wakoo.simplechat;

public final class NoopProcessor implements MessageProcessor {
    public final static NoopProcessor SINGLETON = new NoopProcessor();
    private NoopProcessor() {

    }
}
