package com.group12.stayevrgoe.shared.utils;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author anhvn
 */
@UtilityClass
public class BackgroundUtils {
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
}
