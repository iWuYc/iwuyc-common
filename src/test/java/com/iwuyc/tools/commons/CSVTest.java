package com.iwuyc.tools.commons;

import com.iwuyc.tools.commons.util.file.CharsetDeduce;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;

public class CSVTest {
    @Test
    public void name() throws Exception {
//        final String csvPath = "E:\\IOT\\tmp\\csvFile.csv";
        final String csvPath = "E:\\IOT\\tmp\\csvFile.csv";
        File csvFile = new File(csvPath);
        final String charsetName = CharsetDeduce.charset(csvPath);
        if (null == charsetName) {
            return;
        }
        System.out.println(charsetName);
        Charset charset = Charset.forName(charsetName);
        try (FileInputStream in = new FileInputStream(csvFile); final CSVParser csvRecords = CSVParser.parse(in, charset, CSVFormat.DEFAULT)) {
            for (CSVRecord csvRecord : csvRecords) {
                System.out.println(csvRecord);
            }
        }
    }
}
