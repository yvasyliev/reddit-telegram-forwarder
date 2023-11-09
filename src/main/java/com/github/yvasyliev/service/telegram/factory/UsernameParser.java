package com.github.yvasyliev.service.telegram.factory;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

@Component
public class UsernameParser implements Function<Message, Optional<String>> {
    @Override
    public Optional<String> apply(Message message) {
        if (!message.hasText()) {
            return Optional.empty();
        }

        var username = message.getText().trim();

        var matcher = Pattern
                .compile("https://www\\.reddit\\.com/user/([\\w-]+)")
                .matcher(username);
        if (matcher.find()) {
            username = matcher.group(1);
        }

        matcher = Pattern
                .compile("u/([\\w-]+)")
                .matcher(username);
        if (matcher.matches()) {
            username = matcher.group(1);
        }

        return username.matches("[\\w-]+")
                ? Optional.of(username)
                : Optional.empty();
    }
}
