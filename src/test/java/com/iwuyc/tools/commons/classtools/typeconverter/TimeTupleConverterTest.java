package com.iwuyc.tools.commons.classtools.typeconverter;

import com.iwuyc.tools.commons.basic.type.TimeTuple;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TimeTupleConverterTest {
    @Test
    public void name() {
        final TimeTupleConverter timeTupleConverter = new TimeTupleConverter();
        Assert.assertTrue(timeTupleConverter.isSupport(TimeTuple.class));
        final TimeTuple result = timeTupleConverter.convert("1m", TimeTuple.class);
        Assert.assertTrue(result.getTime() == 1 && result.getTimeUnit() == TimeUnit.MINUTES);
    }
}