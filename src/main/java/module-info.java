module com.wakoo.simplechat {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.wakoo.simplechat to javafx.fxml;
    exports com.wakoo.simplechat;
    opens com.wakoo.simplechat.messages.generators to javafx.fxml;
    exports com.wakoo.simplechat.messages.generators;
    exports com.wakoo.simplechat.messages.processors;
    opens com.wakoo.simplechat.messages.processors to javafx.fxml;
    exports com.wakoo.simplechat.displays;
    opens com.wakoo.simplechat.displays to javafx.fxml;
    exports com.wakoo.simplechat.networking;
    opens com.wakoo.simplechat.networking to javafx.fxml;
    exports com.wakoo.simplechat.gui;
    opens com.wakoo.simplechat.gui to javafx.fxml;
    exports com.wakoo.simplechat.messages;
    opens com.wakoo.simplechat.messages to javafx.fxml;
    exports com.wakoo.simplechat.gui.windows;
    opens com.wakoo.simplechat.gui.windows to javafx.fxml;
}