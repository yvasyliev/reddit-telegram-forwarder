package com.github.yvasyliev.bots.telegram.notifier;

import com.github.yvasyliev.bots.telegram.AbstractRedTelBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class AdminNotifier extends AbstractRedTelBot implements TelegramNotifier {
    @Value("""
            ```
            %s
            ```""")
    private String messageTemplate;

    @Value("4096")
    private int charactersLimit;

    public AdminNotifier(@Value("${BOT_TOKEN}") String botToken) {
        super(botToken);
    }

    @Override
    public Message applyWithException(String text) throws TelegramApiException {
        if (text.length() > charactersLimit) {
            text = text.substring(0, charactersLimit);
        }
        SendMessage sendMessage = SendMessage.builder()
                .chatId(getAdminId())
                .text(messageTemplate.formatted(text))
                .parseMode(ParseMode.MARKDOWN)
                .build();
        return execute(sendMessage);
    }
}
