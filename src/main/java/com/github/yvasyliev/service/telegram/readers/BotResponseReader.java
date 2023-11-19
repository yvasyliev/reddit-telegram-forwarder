package com.github.yvasyliev.service.telegram.readers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.function.ThrowingFunction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class BotResponseReader implements ThrowingFunction<String, String> {
    @Override
    public String applyWithException(String file) throws IOException {
        try (var inputStream = new ClassPathResource(file).getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                    .replace(".", "\\.")
                    .replace("!", "\\!")
                    .replace("-", "\\-");
        }
    }
}
