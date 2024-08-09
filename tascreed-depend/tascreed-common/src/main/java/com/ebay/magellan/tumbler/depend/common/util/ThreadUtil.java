package com.ebay.magellan.tumbler.depend.common.util;

import com.ebay.magellan.tumbler.depend.common.exception.TumblerErrorEnum;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerException;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerExceptionBuilder;

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
            throws TumblerException {
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
            TumblerExceptionBuilder.throwTumblerException(
                    TumblerErrorEnum.TUMBLER_FATAL_EXCEPTION, e.getMessage());
        }
        return results;
    }
}
