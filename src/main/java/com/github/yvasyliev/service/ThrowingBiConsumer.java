package com.github.yvasyliev.service;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface ThrowingBiConsumer<T, U> extends BiConsumer<T, U> {
    void acceptWithException(T t, U u) throws Exception;

    @Override
    default void accept(T t, U u) {
        try {
            acceptWithException(t, u);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
