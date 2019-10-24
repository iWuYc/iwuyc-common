package com.iwuyc.tools.commons.thread.impl;

import org.junit.Test;

public class DefaultThreadPoolServiceHolderTest {

    @Test
    public void test() {
        DefaultThreadFactory factory = new DefaultThreadFactory("");
        System.out.println(factory);
        Thread t = factory.newThread(new Runnable() {
            @Override
            public void run() {

            }
        });
        System.out.println(t.getName());
    }
}