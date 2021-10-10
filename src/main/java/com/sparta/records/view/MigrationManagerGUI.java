package com.sparta.records.view;

import com.sparta.records.LoadBalancer;
import com.sparta.records.util.CSVReader;
import com.sparta.records.util.LogReader;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;

public class MigrationManagerGUI implements Initializable {
    private static String className = MigrationManagerGUI.class.getCanonicalName();
    private static Logger logger = Logger.getLogger(className);

    List<String> PERFMODE = FXCollections.observableArrayList(
            Arrays.asList("Max Performance", "Balanced", "Single Thread"));
    private final String CSV_PATH = "src/main/EmployeeRecordsLarge.csv";

    @FXML
    ComboBox perfMode;

    @FXML
    Tab logTab;

    @FXML
    Button migrationBTN;

    @FXML
    TableColumn levelCol, timeStampCol, packageCol, methodCol, logCol;

    @FXML
    TableView<LogReader.LogEntry> logTable;

    @FXML
    TabPane systemTab;

    private CSVReader csvReader;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        perfMode.getItems().addAll(PERFMODE);
        perfMode.getSelectionModel().selectFirst();

        csvReader = CSVReader.getInstance(CSV_PATH);

        //setting logging table column properties
        timeStampCol.setCellValueFactory(new PropertyValueFactory<>("timeStampCol"));
        logCol.setCellValueFactory(new PropertyValueFactory<>("logCol"));
        packageCol.setCellValueFactory(new PropertyValueFactory<>("packageCol"));
        levelCol.setCellValueFactory(new PropertyValueFactory<>("levelCol"));
        methodCol.setCellValueFactory(new PropertyValueFactory<>("methodCol"));

        systemTab.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if(newTab.equals (logTab)) {
                System.out.println("Log tab");
                logTable.getItems().clear();

                //populating table with logs
                logTable.setItems(LogReader.getInstance().getSystemLogs());
                logTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            }
        });
    }

    public class CSVCallable implements Callable {
        @Override
        public CSVReader.ReadSummary call() throws Exception {
            return CSVReader.getInstance(CSV_PATH).parseRecordsFromCSV();
        }
    }

    private LoadBalancer.Performance getChoice() {
        LoadBalancer.Performance mode;
        switch (perfMode.getSelectionModel().getSelectedIndex()) {
            case 0 -> mode = LoadBalancer.Performance.MAX_PERFORMANCE;
            case 1 -> mode = LoadBalancer.Performance.BALANCED;
            case 2 -> mode = LoadBalancer.Performance.SINGLE_THREAD;
            default -> throw new IllegalStateException(
                    "Unexpected value: " + perfMode.getSelectionModel().getSelectedIndex());
        }
        return mode;
    }

    private void showDialog(String msg) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Dialog");
        ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.setContentText(msg);
        dialog.getDialogPane().getButtonTypes().add(type);

        dialog.showAndWait();
    }

    @FXML
    public void startMigration() {
        //parse CSV file in callable to free UI Thread
        try {
            Future<CSVReader.ReadSummary> completableFuture = CompletableFuture
                    .completedFuture(csvReader.parseRecordsFromCSV());

            //show CSV summary to user in dialog
            showDialog(completableFuture.get().toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //start writing to the database
        try {
            LoadBalancer loadBalancer = new LoadBalancer(getChoice(), csvReader.getAllRecordsWithDuplicateID());
            long startTime = System.nanoTime();
            loadBalancer.createWorkers().stream().forEach((t) -> System.out.println(t));

            final String res = "Whole operation took " + ((System.nanoTime() - startTime) / 1000000) + "ms to complete";
            showDialog(res);

            logger.debug(res);
        } catch (Exception e) {
            logger.fatal(e);
        }
    }

    private void showInTable() {
        
    }
}
