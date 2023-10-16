package com.github.yvasyliev.service.telegram.posts;


import com.github.yvasyliev.model.dto.Post;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Service("TEXT")
public class TextPostService extends PostService<Message> {
    @Override
    public Optional<Message> applyWithException(String chatId, Post post) throws TelegramApiException {
        return Optional.ofNullable(redTelBot.execute(new SendMessage(chatId, post.getText())));
    }
}
