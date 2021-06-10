package com.iwuyc.tools.commons.basic.thread;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.ThreadFactory;

@Data
@Builder
public class ThreadFactoryConf {
    private String threadPoolsName;
    private boolean daemon;
    private boolean forSchedulePools;

    public ThreadFactory buildFactory() {
        return new DefaultThreadFactory(this);
    }
}
