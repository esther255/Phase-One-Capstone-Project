module com.igirepay.igirepay {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.igirepay.igirepay to javafx.fxml;
    exports com.igirepay.igirepay;
    exports com.igirepay;
}