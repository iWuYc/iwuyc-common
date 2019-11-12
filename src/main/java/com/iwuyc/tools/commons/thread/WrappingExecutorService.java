package com.iwuyc.tools.commons.thread;

import com.iwuyc.tools.commons.thread.conf.ThreadPoolConfig;
import com.iwuyc.tools.commons.thread.impl.bean.JDKExecutorServiceTuple;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.ArrayList;
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
@Slf4j
public class WrappingExecutorService<Delegate extends ExecutorService> implements RefreshableExecutorService<Delegate, ThreadPoolConfig> {
    /**
     * 代理的原子引用类型
     */
    private final AtomicReference<Delegate> delegateReference = new AtomicReference<>();
    private final BlockingQueue<Delegate> oldDelegateReference = new ArrayBlockingQueue<>(10);
    private final AtomicReference<ThreadPoolConfig> threadPoolConfig = new AtomicReference<>();
    /**
     * 加上读写锁，避免在替换线程池的时候，使用旧的线程池继续执行后续的任务。
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();


    protected WrappingExecutorService(Delegate delegate, ThreadPoolConfig threadPoolConfig) {
        this.threadPoolConfig.set(threadPoolConfig);
        this.delegateReference.set(delegate);
    }

    public static WrappingExecutorService<ScheduledExecutorService> create(JDKExecutorServiceTuple jdkExecutorServiceTuple) {
        return new WrappingExecutorService<>(jdkExecutorServiceTuple.getScheduledExecutorService(), jdkExecutorServiceTuple.getConfig());
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException("不支持关闭线程池操作。");
    }

    @Override
    @Nonnull
    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException("不支持关闭线程池操作。");
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
        // TODO Neil 更新之后需要调用　release　方法释放旧的代理实例
        try {
            this.lock.writeLock().lock();
            Delegate oldDelegate = this.delegateReference.getAndUpdate((old) -> newDelegate);
            if (!oldDelegate.isShutdown() && newDelegate != oldDelegate) {
                this.oldDelegateReference.add(oldDelegate);
            }
        } catch (RuntimeException ignore) {
        } finally {
            this.lock.writeLock().unlock();
        }
        return true;
    }

    @Override
    public boolean release() {
        try {
            this.lock.writeLock().lock();
            Collection<Delegate> oldDelegates = new ArrayList<>();
            this.oldDelegateReference.drainTo(oldDelegates);
            for (Delegate oldDelegate : oldDelegates) {
                if (null != oldDelegate && !oldDelegate.isShutdown()) {
                    oldDelegate.shutdown();
                }
            }
            return true;
        } catch (RuntimeException e) {
            log.warn("Shutdown threadPool raise an error.Cause:{}", e.getMessage());
            log.debug("Error Detail:", e);
        } finally {
            this.lock.writeLock().unlock();
        }
        return false;
    }

    @Override
    public Delegate delegate() {
        return this.delegateReference.get();
    }

    @Override
    public ThreadPoolConfig config() {
        return threadPoolConfig.get();
    }

    @Override
    public ThreadPoolConfig updateConfig(ThreadPoolConfig newConfig) {
        return threadPoolConfig.getAndSet(newConfig);
    }
}
