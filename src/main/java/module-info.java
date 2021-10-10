module com.spartaglobal {
    requires javafx.controls;
    requires javafx.fxml;
    requires log4j;
    requires java.sql;

    exports com.sparta.records;
    opens com.sparta.records to javafx.graphics;

    opens com.sparta.records.view to javafx.fxml;
    exports com.sparta.records.view;

    exports com.sparta.records.util;
    opens com.sparta.records.util to javafx.fxml;
}