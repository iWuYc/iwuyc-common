package com.iwuyc.tools.commons.util.thread;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class ThreadUtilsTest {

    @Test
    public void callLocationInfo() {
        final Optional<StackTraceElement> stackTraceElementOptional = ThreadUtils.callLocationInfo();
        assertTrue(stackTraceElementOptional.isPresent() && ThreadUtilsTest.class.getName().equals(stackTraceElementOptional.get().getClassName()));
    }
}