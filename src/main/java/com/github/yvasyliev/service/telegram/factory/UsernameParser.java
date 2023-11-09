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
        return Optional
                .ofNullable(message.getText())
                .map(String::trim)
                .map(username -> {
                    var matcher = Pattern
                            .compile("https://www\\.reddit\\.com/user/([\\w-]+)")
                            .matcher(username);
                    if (matcher.find()) {
                        return matcher.group(1);
                    }

                    matcher = Pattern
                            .compile("u/([\\w-]+)")
                            .matcher(username);
                    if (matcher.matches()) {
                        return matcher.group(1);
                    }

                    return username.matches("[\\w-]+")
                            ? username
                            : null;
                });
    }
}
