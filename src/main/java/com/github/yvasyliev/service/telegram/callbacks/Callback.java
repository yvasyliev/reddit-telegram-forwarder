package com.github.yvasyliev.service.telegram.callbacks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.bots.telegram.RedTelBot;
import com.github.yvasyliev.service.telegram.readers.BotResponseReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.function.ThrowingConsumer;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.io.IOException;
import java.net.URISyntaxException;

public abstract class Callback implements ThrowingConsumer<CallbackQuery> {
    @Autowired
    protected RedTelBot redTelBot;

    @Autowired
    protected BotResponseReader responseReader;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String format(String response, Object... args) throws URISyntaxException, IOException {
        return responseReader.applyWithException(response).formatted(args);
    }
}
