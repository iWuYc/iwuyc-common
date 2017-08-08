package com.iwuyc.tools.commons.thread;

import java.util.concurrent.ExecutorService;

public interface ThreadPoolsService
{
    ExecutorService getExecutorService(Class<?> clazz);
}
