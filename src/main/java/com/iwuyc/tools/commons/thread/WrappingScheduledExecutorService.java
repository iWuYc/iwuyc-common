package com.iwuyc.tools.commons.thread;

import javax.annotation.Nonnull;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 可提交线程池代理封装类
 *
 * @author Neil
 */
public class WrappingScheduledExecutorService extends WrappingExecutorService<ScheduledExecutorService> implements ScheduledExecutorService {

    public WrappingScheduledExecutorService(ScheduledExecutorService delegate) {
        super(delegate);
    }

    @Override
    @Nonnull
    public ScheduledFuture<?> schedule(@Nonnull Runnable command, long delay, @Nonnull TimeUnit unit) {
        return super.getDelegate().schedule(command, delay, unit);
    }

    @Override
    @Nonnull
    public <V> ScheduledFuture<V> schedule(@Nonnull Callable<V> callable, long delay, @Nonnull TimeUnit unit) {
        return super.getDelegate().schedule(callable, delay, unit);
    }

    @Override
    @Nonnull
    public ScheduledFuture<?> scheduleAtFixedRate(@Nonnull Runnable command, long initialDelay, long period, @Nonnull TimeUnit unit) {
        return super.getDelegate().scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    @Override
    @Nonnull
    public ScheduledFuture<?> scheduleWithFixedDelay(@Nonnull Runnable command, long initialDelay, long delay, @Nonnull TimeUnit unit) {
        return super.getDelegate().scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }
}
