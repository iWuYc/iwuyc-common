package com.iwuyc.tools.commons.thread;

import com.iwuyc.tools.commons.thread.conf.ThreadPoolConfig;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 线程池代理类
 *
 * @author Neil
 */
public class WrappingExecutorService<Delegate extends ExecutorService> implements RefreshableExecutorService<Delegate, ThreadPoolConfig> {
    /**
     * 代理的原子引用类型
     */
    private final AtomicReference<Delegate> delegateReference = new AtomicReference<>();
    private final ThreadPoolConfig threadPoolConfig;
    /**
     * 加上读写锁，避免在替换线程池的时候，使用旧的线程池继续执行后续的任务。
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();


    public WrappingExecutorService(Delegate delegate, ThreadPoolConfig threadPoolConfig) {
        this.threadPoolConfig = threadPoolConfig;
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
        try {
            this.lock.readLock().lock();
            return this.delegateReference.get();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public boolean refresh(Delegate newDelegate) {
        try {
            this.lock.writeLock().lock();
            Delegate oldDelegate = this.delegateReference.getAndUpdate((old) -> newDelegate);
            if (!oldDelegate.isShutdown() && newDelegate != oldDelegate) {
                oldDelegate.shutdown();
            }
        } catch (RuntimeException ignore) {
        } finally {
            this.lock.writeLock().unlock();
        }
        return true;
    }

    @Override
    public Delegate delegate() {
        return this.delegateReference.get();
    }

    @Override
    public ThreadPoolConfig config() {
        return threadPoolConfig;
    }
}
