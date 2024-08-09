package com.ebay.magellan.tascreed.depend.common.thread;

import com.ebay.magellan.tascreed.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tascreed.depend.common.util.ExceptionUtil;
import com.ebay.magellan.tascreed.depend.common.util.HostUtil;
import lombok.Getter;

import java.util.concurrent.*;

@Getter
public class DefaultThreadPoolExecutor<T extends Runnable> {
    private final String THIS_CLASS_NAME = this.getClass().getSimpleName();
    ThreadPoolExecutor delegate;
    int maxWorkerCount;
    TumblerLogger logger;
    volatile boolean closed = false;

    public DefaultThreadPoolExecutor(int maxWorkerCount, ThreadFactory threadFactory, TumblerLogger logger) {
        this.maxWorkerCount = maxWorkerCount;
        this.logger = logger;
        init(threadFactory);
    }

    public void init(ThreadFactory threadFactory) {
        int nThreads = maxWorkerCount > 0 ? maxWorkerCount : 1;
        this.delegate = new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1),
                threadFactory);
        this.closed = false;

        logger.info(THIS_CLASS_NAME, String.format("%s inited", THIS_CLASS_NAME));
    }

    public synchronized boolean resetMaxWorkerCount(int newMaxWorkerCount) {
        if (newMaxWorkerCount < 0) return false;
        if (maxWorkerCount == newMaxWorkerCount) return false;
        if (maxWorkerCount < newMaxWorkerCount) {
            delegate.setMaximumPoolSize(newMaxWorkerCount);
            delegate.setCorePoolSize(newMaxWorkerCount);
        } else if (maxWorkerCount > newMaxWorkerCount) {
            delegate.setCorePoolSize(newMaxWorkerCount);
            delegate.setMaximumPoolSize(newMaxWorkerCount);
        }
        logger.info(THIS_CLASS_NAME, String.format(
                "max pool size is reset from %d to %d", maxWorkerCount, newMaxWorkerCount));
        this.maxWorkerCount = newMaxWorkerCount;
        return true;
    }

    public synchronized boolean hasVacancy() {
        return getActiveThreadCount() < maxWorkerCount;
    }

    public synchronized boolean poolSizeExceeded() {
        return getActiveThreadCount() > maxWorkerCount;
    }

    public int getActiveThreadCount() {
        return delegate.getActiveCount();
    }

    public Future<?> submit(T runnable) {
        try {
            if (hasVacancy()) {
                String msg = String.format("In host %s, %s will submit a new thread",
                        HostUtil.getHostName(), THIS_CLASS_NAME);
                logger.info(THIS_CLASS_NAME, msg);
                return delegate.submit(runnable);
            } else {
                /**
                 * If thread pool is full, just exit.
                 * Other hosts which have idle threads will take over the free packs.
                 */
                String msg = String.format("In host %s, thread pool is full, %s can't submit new threads",
                        HostUtil.getHostName(), THIS_CLASS_NAME);
                logger.info(THIS_CLASS_NAME, msg);
            }
        } catch (Exception e) {
            logger.error(THIS_CLASS_NAME, ExceptionUtil.getStackTrace(e));
        }
        return null;
    }

    // only closed the thread pool executor, not terminate threads
    public void close(long waitTimeInSeconds) {
        if (delegate != null) {
            delegate.shutdown();
            try {
                if (!delegate.awaitTermination(waitTimeInSeconds, TimeUnit.SECONDS)) {
                    delegate.shutdownNow();
                }
            } catch (InterruptedException e) {
                logger.warn(THIS_CLASS_NAME, String.format("interrupted when await termination: %s", e.getMessage()));
                delegate.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        this.delegate = null;
        this.closed = true;

        logger.info(THIS_CLASS_NAME, String.format("%s closed", THIS_CLASS_NAME));
    }
}
