package com.github.yvasyliev.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramLoggerBotImpl extends AbstractTelegramBot implements TelegramLoggerBot {
    @Value("${DEVELOPER_ID}")
    private String developerId;

    @Value("""
            ```
            %s
            ```""")
    private String logTemplate;

    public TelegramLoggerBotImpl(String botToken) {
        super(botToken);
    }

    @Override
    public void log(String text) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(developerId)
                .text(logTemplate.formatted(text))
                .parseMode(ParseMode.MARKDOWN)
                .build();
        execute(sendMessage);
    }
}
