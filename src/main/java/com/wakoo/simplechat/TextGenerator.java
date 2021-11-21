package com.wakoo.simplechat;

public class TextGenerator extends MessageGenerator {
    TextGenerator(String text) {
        super(MessageDispatcher.MessageTypes.MessageText);
        insertString(text, false);
        Finish();
    }
}
