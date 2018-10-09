package com.iwuyc.tools.commons.basic.type;

import org.junit.Test;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class TimeTupleTest {
    @Test
    public void test1() {
        TimeTuple timeTuple = TimeTuple.create(10, ChronoUnit.SECONDS);
        System.out.println(timeTuple);
        timeTuple = TimeTuple.create(10, TimeUnit.SECONDS);
        System.out.println(timeTuple);
    }
}