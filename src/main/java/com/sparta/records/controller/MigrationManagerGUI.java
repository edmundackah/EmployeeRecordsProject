package com.sparta.records.controller;

import com.sparta.records.models.Employee;
import com.sparta.records.util.CSVReader;
import com.sparta.records.util.LogReader;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    TableColumn employeeIDCol, titleCol, firstNameCol, initialCol;

    @FXML
    TableColumn lastNameCol, genderCol,emailCol, dobCol, startCol, salaryCol;

    @FXML
    TableView<LogReader.LogEntry> logTable;

    @FXML
    TableView resultTable;

    @FXML
    TabPane systemTab;

    private CSVReader csvReader;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        perfMode.getItems().addAll(PERFMODE);
        perfMode.getSelectionModel().selectFirst();

        csvReader = CSVReader.getInstance(CSV_PATH);

        //setting result table column properties
        employeeIDCol.setCellValueFactory(new PropertyValueFactory<>("employeeIDCol"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("titleCol"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstNameCol"));
        initialCol.setCellValueFactory(new PropertyValueFactory<>("initialCol"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastNameCol"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("genderCol"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("emailCol"));
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dobCol"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startCol"));
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("salaryCol"));


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

        showInTable();
    }

    public class EmployeeEntry {
        public SimpleIntegerProperty employeeIDCol, salaryCol;
        public SimpleStringProperty titleCol, firstNameCol, initialCol, lastNameCol;
        public SimpleStringProperty emailCol, dobCol, startCol, genderCol;

        public EmployeeEntry(Employee e) {
            this.employeeIDCol = new SimpleIntegerProperty(e.getEmployeeID());
            this.salaryCol = new SimpleIntegerProperty(e.getSalary());

            this.titleCol = new SimpleStringProperty(e.getTitle());
            this.firstNameCol = new SimpleStringProperty(e.getFirstName());
            this.initialCol = new SimpleStringProperty(e.getInitial());
            this.lastNameCol = new SimpleStringProperty(e.getLastName());

            this.emailCol = new SimpleStringProperty(e.getEmail());
            this.dobCol = new SimpleStringProperty(e.getDob().toString());
            this.startCol = new SimpleStringProperty(e.getStartDate().toString());
            this.genderCol = new SimpleStringProperty(e.getGender());
        }

        public int getEmployeeIDCol() {
            return employeeIDCol.get();
        }

        public SimpleIntegerProperty employeeIDColProperty() {
            return employeeIDCol;
        }

        public int getSalaryCol() {
            return salaryCol.get();
        }

        public SimpleIntegerProperty salaryColProperty() {
            return salaryCol;
        }

        public String getTitleCol() {
            return titleCol.get();
        }

        public SimpleStringProperty titleColProperty() {
            return titleCol;
        }

        public String getFirstNameCol() {
            return firstNameCol.get();
        }

        public SimpleStringProperty firstNameColProperty() {
            return firstNameCol;
        }

        public String getInitialCol() {
            return initialCol.get();
        }

        public SimpleStringProperty initialColProperty() {
            return initialCol;
        }

        public String getLastNameCol() {
            return lastNameCol.get();
        }

        public SimpleStringProperty lastNameColProperty() {
            return lastNameCol;
        }

        public String getEmailCol() {
            return emailCol.get();
        }

        public SimpleStringProperty emailColProperty() {
            return emailCol;
        }

        public String getDobCol() {
            return dobCol.get();
        }

        public SimpleStringProperty dobColProperty() {
            return dobCol;
        }

        public String getStartCol() {
            return startCol.get();
        }

        public SimpleStringProperty startColProperty() {
            return startCol;
        }

        public String getGenderCol() {
            return genderCol.get();
        }

        public SimpleStringProperty genderColProperty() {
            return genderCol;
        }
    }

    private void showInTable() {
        ObservableList<EmployeeEntry> sortResults = FXCollections.observableArrayList();
        resultTable.getItems().clear();

        csvReader.getAllRecordsWithDuplicateID().forEach((s) -> sortResults.add(new EmployeeEntry(s)));

        resultTable.setItems(sortResults);
        resultTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
}
