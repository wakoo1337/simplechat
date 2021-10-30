module com.wakoo.simplechat {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.wakoo.simplechat to javafx.fxml;
    exports com.wakoo.simplechat;
}