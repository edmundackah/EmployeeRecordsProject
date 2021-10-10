package com.sparta.records.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;

public class JavaFxApp extends Application {
    private static String className = JavaFxApp.class.getCanonicalName();
    private static Logger logger = Logger.getLogger(className);

    @Override
    public void start(Stage stage) throws IOException {
        PropertyConfigurator.configure("log4j.properties");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/system_ui.fxml"));
            Scene scene = new Scene(root, 962, 483);

            stage.setTitle("Sort Manager GUI");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch();
    }
}
