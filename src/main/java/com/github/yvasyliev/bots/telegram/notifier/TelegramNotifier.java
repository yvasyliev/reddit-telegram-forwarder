package com.github.yvasyliev.bots.telegram.notifier;

import org.springframework.util.function.ThrowingFunction;
import org.telegram.telegrambots.meta.api.objects.Message;

@FunctionalInterface
public interface TelegramNotifier extends ThrowingFunction<String, Message> {
}
