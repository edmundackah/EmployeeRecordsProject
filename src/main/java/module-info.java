module com.spartaglobal {
    requires javafx.controls;
    requires javafx.fxml;
    requires log4j;
    requires java.sql;

    opens com.sparta.records.view to javafx.fxml;
    exports com.sparta.records.view;

    exports com.sparta.records.util;
    opens com.sparta.records.util to javafx.fxml, javafx.graphics;
    exports com.sparta.records.models;
    opens com.sparta.records.models to javafx.graphics;
    exports com.sparta.records.controller;
    opens com.sparta.records.controller to javafx.fxml, javafx.graphics;
}