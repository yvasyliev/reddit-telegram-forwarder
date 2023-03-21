package com.github.yvasyliev.service.reddit.api;

import java.io.IOException;

@FunctionalInterface
public interface Request<T> {
    T execute() throws IOException, InterruptedException;
}
