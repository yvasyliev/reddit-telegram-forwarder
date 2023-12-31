package com.github.yvasyliev.bots.telegram.notifier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class AdminNotifier extends DefaultAbsSender implements TelegramNotifier {
    @Value("4096")
    private int charactersLimit;

    @Value("${telegram.admin.id}")
    private String adminId;

    @Value("""
            ```
            %s
            ```""")
    private String messageTemplate;

    public AdminNotifier(
            @Value("#{new org.telegram.telegrambots.bots.DefaultBotOptions()}") DefaultBotOptions botOptions,
            @Value("${telegram.bot.token}") String botToken
    ) {
        super(botOptions, botToken);
    }

    @Override
    @NonNull
    public Message applyWithException(String text) throws TelegramApiException {
        if (text.length() > charactersLimit) {
            text = text.substring(0, charactersLimit);
        }
        SendMessage sendMessage = SendMessage.builder()
                .chatId(adminId)
                .text(messageTemplate.formatted(text))
                .parseMode(ParseMode.MARKDOWN)
                .build();
        return execute(sendMessage);
    }
}
