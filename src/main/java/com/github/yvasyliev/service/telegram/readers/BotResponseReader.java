package com.github.yvasyliev.service.telegram.readers;

import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingFunction;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class BotResponseReader implements ThrowingFunction<String, String> {
    @Override
    public String applyWithException(String file) throws URISyntaxException, IOException {
        return String.join(
                        "\n",
                        Files.readAllLines(Paths.get(Objects.requireNonNull(BotResponseReader.class.getClassLoader().getResource(file)).toURI()))
                )
                .replace(".", "\\.")
                .replace("!", "\\!")
                .replace("-", "\\-");
    }
}
