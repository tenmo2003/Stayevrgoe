package com.group12.stayevrgoe.shared.utils;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author anhvn
 */
@UtilityClass
public class ThreadPoolUtils {
    private static final ThreadPoolExecutor EXECUTOR =
            new ThreadPoolExecutor(
                    5,
                    5,
                    1L,
                    TimeUnit.SECONDS,
                    new java.util.concurrent.LinkedBlockingQueue<>());

    public static void executeTask(Runnable runnable) {
        EXECUTOR.execute(runnable);
    }

    public static <E> List<Future<E>> executeAndWaitForTasks(List<Callable<E>> tasks) throws InterruptedException {
        return EXECUTOR.invokeAll(tasks);
    }
}
