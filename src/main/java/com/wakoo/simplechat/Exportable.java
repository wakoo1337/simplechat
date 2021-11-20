package com.wakoo.simplechat;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public interface Exportable {
    ArrayList<ByteBuffer> export();
}
