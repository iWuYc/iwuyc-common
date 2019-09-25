package com.iwuyc.tools.commons.util;

import com.google.common.base.Stopwatch;
import org.junit.Test;

import java.lang.invoke.SerializedLambda;

public class PlaceholderExtractTest {

    @Test
    public void compile() {
        int times = 1000_0000;
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0; i < times; i++) {
            PlaceholderExtract extract = new PlaceholderExtract("#{#{}#}#");
            extract.compile("#{", "}#");
            extract = new PlaceholderExtract("#{#{}#}#,#{date()}#");
            extract.compile("#{", "}#");
        }
        System.out.println("cost:" + stopwatch.stop());
        //
        //        extract = new PlaceholderExtract("#{#{}#");
        //        System.out.println(extract.compile("#{", "}#"));
    }

}
