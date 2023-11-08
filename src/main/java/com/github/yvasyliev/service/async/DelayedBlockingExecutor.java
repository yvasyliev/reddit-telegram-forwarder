package com.github.yvasyliev.service.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
public class DelayedBlockingExecutor {
    @Value("#{new java.util.concurrent.LinkedBlockingQueue()}")
    private Queue<Runnable> delayedRunnables;

    public <T> CompletableFuture<T> submit(Callable<T> callable) {
        var completableFuture = new CompletableFuture<T>();
        delayedRunnables.add(() -> {
            try {
                completableFuture.complete(callable.call());
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        });
        return completableFuture;
    }

    @Scheduled(fixedDelayString = "${delayed.blocking.executor.delay.in.seconds:20}", timeUnit = TimeUnit.SECONDS)
    public void runNextRunnable() {
        if (!delayedRunnables.isEmpty()) {
            delayedRunnables.poll().run();
        }
    }
}
