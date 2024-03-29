package com.github.yvasyliev.service.telegram.posts;


import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.model.dto.post.TextPost;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Service(Post.Type.TEXT)
public class TextPostService extends PostService<TextPost, Message> {
    @Override
    @NonNull
    public Optional<Message> applyWithException(@NonNull String chatId, TextPost post) throws TelegramApiException {
        return Optional.ofNullable(redditTelegramForwarderBot.execute(new SendMessage(chatId, post.getText())));
    }
}
