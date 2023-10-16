package com.github.yvasyliev.service.telegram.posts;

import com.github.yvasyliev.model.dto.Post;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Service("POLL")
public class PollPostService extends PostService<Message> {
    @Override
    public Optional<Message> applyWithException(String chatId, Post post) throws TelegramApiException {
        var sendPoll = SendPoll.builder()
                .chatId(chatId)
                .question(post.getText())
                .options(post.getOptions())
                .build();

        return Optional.ofNullable(redTelBot.execute(sendPoll));
    }
}
