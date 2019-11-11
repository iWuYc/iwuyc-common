package com.iwuyc.tools.commons.thread.impl.bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public interface ExecutorServiceTuple {

    ExecutorService getExecutorService();
     ScheduledExecutorService getScheduledExecutorService();
}
