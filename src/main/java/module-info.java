module com.igirepay.igirepay {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.postgresql.jdbc;

    opens com.igirepay to javafx.fxml;
    opens com.igirepay.LAB1_model to javafx.fxml, javafx.base;
    opens com.igirepay.LAB1_service to javafx.fxml;
    opens com.igirepay.LAB2_dao to javafx.fxml;
    opens com.igirepay.LAB3_ui to javafx.fxml;
    opens com.igirepay.LAB3_util to javafx.fxml;
    opens com.igirepay.LAB3_exception to javafx.fxml;

    exports com.igirepay;
    exports com.igirepay.LAB1_model;
    exports com.igirepay.LAB2_dao;
    exports com.igirepay.LAB1_service;
    exports com.igirepay.LAB3_ui;
    exports com.igirepay.LAB3_util;
    exports com.igirepay.LAB3_exception;
}