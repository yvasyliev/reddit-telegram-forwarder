package com.github.yvasyliev.service.telegram.readers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingFunction;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

@Service
public class BotResponseReader implements ThrowingFunction<String, String> {
    @Override
    public String applyWithException(String file) throws URISyntaxException, IOException {
        return Files.readString(new ClassPathResource(file).getFile().toPath())
                .replace(".", "\\.")
                .replace("!", "\\!")
                .replace("-", "\\-");
    }
}
