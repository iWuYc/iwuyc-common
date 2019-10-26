package com.iwuyc.tools.commons.thread;

import java.util.concurrent.ScheduledExecutorService;

/**
 * 可刷新的提交式线程池服务实现
 *
 * @author Neil
 */
public interface RefreshableScheduledExecutorService extends RefreshableExecutorService<ScheduledExecutorService>, ScheduledExecutorService {
}
