package com.iwuyc.tools.commons;

import com.iwuyc.tools.commons.util.file.CharsetDeduce;
import com.iwuyc.tools.commons.util.file.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

@Slf4j
public class CSVTest {
    @Test
    public void name() throws Exception {
        final String csvPath = FileUtil.absoluteLocation("classpath:/testfile/csv-gbk.csv");
        File csvFile = new File(csvPath);
        final String charsetName = CharsetDeduce.charset(csvPath);
        assertEquals("GBK", charsetName);
        Charset charset = Charset.forName(charsetName);
        try (FileInputStream in = new FileInputStream(csvFile); final CSVParser csvRecords = CSVParser.parse(in, charset, CSVFormat.DEFAULT)) {
            for (CSVRecord csvRecord : csvRecords) {
                log.debug("csvRecord:{}", csvRecord);
            }
        }
    }
}
