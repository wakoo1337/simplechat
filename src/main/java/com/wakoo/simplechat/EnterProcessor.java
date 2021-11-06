package com.wakoo.simplechat;

public class EnterProcessor implements MessageProcessor {
    public final static EnterProcessor SINGLETON = new EnterProcessor();
    private EnterProcessor() {

    }
    public final String marker = ">>>";
}
