package com.github.yvasyliev.bots.telegram.notifier;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@FunctionalInterface
public interface TelegramNotifier {
    Message notify(String text) throws TelegramApiException;
}
