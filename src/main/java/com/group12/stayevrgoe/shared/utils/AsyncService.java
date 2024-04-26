package com.group12.stayevrgoe.shared.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author anhvn
 */
@Component
public class AsyncService {
    private final ThreadPoolExecutor executor =
            new ThreadPoolExecutor(
                    1,
                    10,
                    0L,
                    java.util.concurrent.TimeUnit.MILLISECONDS,
                    new java.util.concurrent.LinkedBlockingQueue<>());

    public void runAsync(Runnable runnable) {
        executor.execute(runnable);
    }
}
