module com.wakoo.simplechat {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires io.netty.common;
    requires io.netty.buffer;
    requires io.netty.transport;

    opens com.wakoo.simplechat to javafx.fxml;
    exports com.wakoo.simplechat;
}