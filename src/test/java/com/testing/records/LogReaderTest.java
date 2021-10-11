package com.testing.records;

import com.sparta.records.util.LogReader;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogReaderTest {
    private static Logger logger = Logger.getLogger("Unit Test");

    @Test
    public void GivenLogFileIsNotEmpty_ReturnListOfLogEntry() {
        LogReader logReader = LogReader.getInstance();
        assertEquals(false, logReader.getSystemLogs().isEmpty());
    }

    @Test
    public void GivenLogEntry_GetPackage() {
        LogReader logReader = LogReader.getInstance();
        assertEquals(false, logReader.getSystemLogs().get(0).getPackageCol().isEmpty());
    }

    @Test
    public void GivenLogEntry_GetMethod() {
        LogReader logReader = LogReader.getInstance();
        assertEquals(false, logReader.getSystemLogs().get(2).getMethodCol().isEmpty());
    }


    @Test
    public void GivenLogEntry_GetLogMessage() {
        LogReader logReader = LogReader.getInstance();
        assertEquals(false, logReader.getSystemLogs().get(1).getLogCol().isEmpty());
    }

    @Test
    public void GivenLogEntry_GetLogLevel() {
        LogReader logReader = LogReader.getInstance();
        assertEquals(false, logReader.getSystemLogs().get(3).getLevelCol().isEmpty());
    }

    @Test
    public void GivenLogEntry_GetTimeStamp() {
        LogReader logReader = LogReader.getInstance();
        assertEquals(false, logReader.getSystemLogs().get(0).getTimeStampCol().isEmpty());
    }
}
