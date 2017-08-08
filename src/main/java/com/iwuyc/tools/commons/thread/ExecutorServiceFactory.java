package com.iwuyc.tools.commons.thread;

import java.util.concurrent.ExecutorService;

import com.iwuyc.tools.commons.thread.conf.ThreadPoolConfig;

public interface ExecutorServiceFactory
{
    ExecutorService create(ThreadPoolConfig config);
}
