package com.github.yvasyliev.service.telegram.commands;

import com.github.yvasyliev.bots.telegram.RedditTelegramForwarderBot;
import com.github.yvasyliev.service.telegram.readers.BotResponseReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.util.function.ThrowingConsumer;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

public abstract class Command implements ThrowingConsumer<Message> {
    @Autowired
    protected RedditTelegramForwarderBot redditTelegramForwarderBot;

    @Autowired
    protected BotResponseReader responseReader;

    @Override
    public void acceptWithException(@NonNull Message message) throws Exception {
        if (hasPermission(message)) {
            execute(message);
        }
    }

    protected abstract void execute(Message message) throws Exception;

    protected boolean hasPermission(Message message) {
        return true;
    }

    protected Message reply(Message to, String template, Object... args) throws URISyntaxException, IOException, TelegramApiException {
        var sendMessage = SendMessage.builder()
                .chatId(to.getChatId())
                .text(responseReader.applyWithException(template).formatted(args))
                .disableWebPagePreview(true)
                .parseMode(ParseMode.MARKDOWNV2)
                .build();
        return redditTelegramForwarderBot.execute(sendMessage);
    }
}
