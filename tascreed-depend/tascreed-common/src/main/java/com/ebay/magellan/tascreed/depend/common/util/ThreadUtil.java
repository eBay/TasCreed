package com.ebay.magellan.tascreed.depend.common.util;

import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ThreadUtil {
    public static String getCurrentThreadName() {
        return HostUtil.getHostName() + "/" + Thread.currentThread().getName();
    }

    public static <T> List<T> invokeAllThreads(ExecutorService executorService, List<? extends Callable<T>> threads)
            throws TcException {
        List<T> results = new ArrayList<>();
        try {
            List<Future<T>> futures = executorService.invokeAll(threads);
            for (Future<T> future : futures) {
                T result = future.get();
                if (result != null) {
                    results.add(result);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            TcExceptionBuilder.throwTcException(
                    TcErrorEnum.TC_FATAL_EXCEPTION, e.getMessage());
        }
        return results;
    }
}
