package com.sparta.records.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

;

public class LogReader {
    private static String className = LogReader.class.getCanonicalName();
    private static Logger logger = Logger.getLogger(className);

    private static LogReader instance = null;

    public static class LogEntry {
        public SimpleStringProperty levelCol, timeStampCol, logCol;
        public SimpleStringProperty packageCol, methodCol;

        public LogEntry(String levelCol, String timeStampCol, String logCol, String packageCol, String methodCol) {
            this.levelCol = new SimpleStringProperty(levelCol);
            this.timeStampCol =  new SimpleStringProperty(timeStampCol);
            this.logCol =  new SimpleStringProperty(logCol);
            this.packageCol =  new SimpleStringProperty(packageCol);
            this.methodCol =  new SimpleStringProperty(methodCol);
        }

        public String getLevelCol() {
            return levelCol.get();
        }

        public String getTimeStampCol() {
            return timeStampCol.get();
        }

        public String getLogCol() {
            return logCol.get();
        }

        public String getPackageCol() {
            return packageCol.get();
        }

        public String getMethodCol() {
            return methodCol.get();
        }
    }

    private LogReader() {

    }

    public static LogReader getInstance() {
        return instance == null ? new LogReader() : instance;
    }

    public ObservableList<LogEntry> getSystemLogs() {
        ObservableList<LogEntry> logs = FXCollections.observableArrayList();
        Scanner linReader = null;

        try {
            logger.debug("Reading log file");
            linReader = new Scanner(new File("app.csv"));

            while (linReader.hasNext()) {
                String[] line = linReader.nextLine().split(" ");
                String timeStamp = line[1] + " " + line[2];
                StringBuilder logMsg = new StringBuilder();

                for (int x = 5; x < line.length; x++) {
                    logMsg.append(line[x] + " ");
                }

                logs.add(
                        new LogEntry(line[0], timeStamp, logMsg.toString(), line[3], line[4])
                );
            }
        } catch (IOException e) {
            logger.error(e);
        } finally {
            logger.debug("Closing log file");
            linReader.close();
        }

        return logs;
    }
}
