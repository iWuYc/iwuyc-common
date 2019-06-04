package com.iwuyc.tools.commons.util.file;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class PropertiesFileUtilsTest {

    @Test
    public void name() throws Exception{
//        Thread.sleep(30_000);
        String fileLocation = "G:\\strategy.properties";
        Map<String, Integer> properties = new LinkedHashMap<>();
        int count = 10;
        String iccidTemplate = "12345678900000000000";
        for(int i = 0; i < count; i++){
            String num = String.valueOf(i);
            String iccid = iccidTemplate.substring(0, 20 - num.length()) + num;
            if(i % 3 == 0){
                properties.put(iccid, 1);
            }else {

                properties.put(iccid, 0);
            }
        }
        PropertiesFileUtils.replacePropertiesFile(fileLocation, properties);
    }
}