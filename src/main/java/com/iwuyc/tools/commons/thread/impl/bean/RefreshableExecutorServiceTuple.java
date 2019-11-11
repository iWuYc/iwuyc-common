package com.iwuyc.tools.commons.thread.impl.bean;

import com.iwuyc.tools.commons.thread.RefreshableExecutorService;
import com.iwuyc.tools.commons.thread.RefreshableScheduledExecutorService;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class RefreshableExecutorServiceTuple implements ExecutorServiceTuple {

    private RefreshableExecutorService<?, ?> executorService;
    private RefreshableScheduledExecutorService scheduledExecutorService;

}