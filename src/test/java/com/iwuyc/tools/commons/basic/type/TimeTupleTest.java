package com.iwuyc.tools.commons.basic.type;

import org.junit.Test;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class TimeTupleTest {
    @Test
    public void test1() {
        TimeTuple timeTuple = TimeTuple.create(10, ChronoUnit.SECONDS);
        System.out.println(timeTuple);
        timeTuple = TimeTuple.create(10, TimeUnit.SECONDS);
        System.out.println(timeTuple);
    }
}