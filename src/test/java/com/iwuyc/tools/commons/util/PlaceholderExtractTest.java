package com.iwuyc.tools.commons.util;

import com.google.common.base.Stopwatch;
import com.iwuyc.tools.commons.util.collection.CollectionUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PlaceholderExtractTest {

    public static boolean checkList(List<String> source, List<String> checkList) {
        final int sourceSize = CollectionUtil.sizeOf(source);
        final int checkListSize = CollectionUtil.sizeOf(checkList);
        if (sourceSize != checkListSize) {
            return false;
        }
        for (int i = 0; i < source.size(); i++) {
            if (!Objects.equals(source.get(i), checkList.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Test
    @Ignore("Performance test skip it.")
    public void performance() {
        int times = 1000_0000;
        Stopwatch stopwatch = Stopwatch.createStarted();
        PlaceholderExtract extract = new PlaceholderExtract("#{#{}#}#,#{date()}#");
        for (int i = 0; i < times; i++) {
            List<String> result = extract.compile("#{", "}#");
        }
        System.out.println("cost:" + stopwatch.stop());
        Assert.assertTrue(true);
    }

    @Test
    public void compile() {
        PlaceholderExtract extract = new PlaceholderExtract("#{#{neil}#}#hello}#");
        List<String> result = extract.compile("#{", "}#");
        List<String> checkList = Arrays.asList("#{neil}#", "#{#{neil}#}#");
        Assert.assertTrue(checkList(result, checkList));

        extract = new PlaceholderExtract("#{#{neil}#}#hello#{");

        result = extract.compile("#{", "}#");
        Assert.assertTrue(checkList(result, checkList));

        extract = new PlaceholderExtract("#{#{#{neil}#}#hello");
        result = extract.compile("#{", "}#");
        Assert.assertTrue(checkList(result, checkList));


        extract = new PlaceholderExtract("#{#{neil}#}#hello#{#{jack}#}#");
        result = extract.compile("#{", "}#");
        checkList = Arrays.asList("#{neil}#", "#{#{neil}#}#", "#{jack}#", "#{#{jack}#}#");
        Assert.assertTrue(checkList(result, checkList));

        extract = new PlaceholderExtract("#{#{neil}#}#hello${#{jack}#}$");
        result = extract.compile("#{", "}#");
        checkList = Arrays.asList("#{neil}#", "#{#{neil}#}#", "#{jack}#");
        Assert.assertTrue(checkList(result, checkList));

        result = extract.compile("${", "}$");
        checkList = Collections.singletonList("${#{jack}#}$");
        Assert.assertTrue(checkList(result, checkList));
    }
}
