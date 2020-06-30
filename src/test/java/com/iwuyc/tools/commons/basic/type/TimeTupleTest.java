package com.iwuyc.tools.commons.basic.type;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TimeTupleTest {
    @Test
    public void test1() {
        TimeTuple timeTuple = TimeTuple.create(10, TimeUnit.SECONDS);
        System.out.println(timeTuple);
    }

    @Test
    public void toTime() {
        TimeTuple timeTuple = TimeTuple.create(10, TimeUnit.SECONDS);
        long milliSeconds = timeTuple.toTime(TimeUnit.MILLISECONDS);
        Assert.assertEquals(milliSeconds, 10_000);

    }
}