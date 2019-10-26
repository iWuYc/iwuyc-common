package com.iwuyc.tools.commons.thread;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 线程池代理类
 *
 * @author Neil
 */
public class WrappingExecutorService<Delegate extends ExecutorService> implements RefreshableExecutorService<Delegate> {
    private final AtomicReference<Delegate> delegateReference = new AtomicReference<>();


    public WrappingExecutorService(Delegate delegate) {
        this.delegateReference.set(delegate);
    }


    @Override
    public void shutdown() {
        this.getDelegate().shutdown();
    }

    @Override
    @Nonnull
    public List<Runnable> shutdownNow() {
        return this.getDelegate().shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return this.getDelegate().isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.getDelegate().isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, @Nonnull TimeUnit unit) throws InterruptedException {
        return this.getDelegate().awaitTermination(timeout, unit);
    }

    @Override
    @Nonnull
    public <T> Future<T> submit(@Nonnull Callable<T> task) {
        return this.getDelegate().submit(task);
    }

    @Override
    @Nonnull
    public <T> Future<T> submit(@Nonnull Runnable task, T result) {
        return this.getDelegate().submit(task, result);
    }

    @Override
    @Nonnull
    public Future<?> submit(@Nonnull Runnable task) {
        return this.getDelegate().submit(task);
    }

    @Override
    @Nonnull
    public <T> List<Future<T>> invokeAll(@Nonnull Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.getDelegate().invokeAll(tasks);
    }

    @Override
    @Nonnull
    public <T> List<Future<T>> invokeAll(@Nonnull Collection<? extends Callable<T>> tasks, long timeout, @Nonnull TimeUnit unit) throws InterruptedException {
        return this.getDelegate().invokeAll(tasks, timeout, unit);
    }

    @Override
    @Nonnull
    public <T> T invokeAny(@Nonnull Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.getDelegate().invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(@Nonnull Collection<? extends Callable<T>> tasks, long timeout, @Nonnull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.getDelegate().invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(@Nonnull Runnable command) {
        this.getDelegate().execute(command);
    }

    protected Delegate getDelegate() {
        return this.delegateReference.get();
    }

    @Override
    public boolean refresh(Delegate newDelegate) {
        Delegate oldReference = this.delegateReference.getAndUpdate((old) -> newDelegate);
        try {
            if (!oldReference.isShutdown()) {
                oldReference.shutdown();
            }
        } catch (RuntimeException ignore) {
        }
        return true;
    }
}
