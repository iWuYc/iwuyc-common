package com.iwuyc.tools.commons.thread.conf;

import org.junit.Test;

import static org.junit.Assert.*;

public class UsingConfigTest {

    @Test
    public void create() {
        UsingConfig usingConfig = UsingConfig.create(UsingConfig.class.getName(), "root");
        System.out.println(usingConfig.getDomain());
        System.out.println(usingConfig.getThreadPoolsName());
    }
}