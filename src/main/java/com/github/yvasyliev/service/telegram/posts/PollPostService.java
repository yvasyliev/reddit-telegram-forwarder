package com.github.yvasyliev.service.telegram.posts;

import com.github.yvasyliev.model.dto.post.PollPost;
import com.github.yvasyliev.model.dto.post.Post;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Service(Post.Type.POLL)
public class PollPostService extends PostService<PollPost, Message> {
    @Override
    @NonNull
    public Optional<Message> applyWithException(@NonNull String chatId, PollPost post) throws TelegramApiException {
        var sendPoll = SendPoll.builder()
                .chatId(chatId)
                .question(post.getText())
                .options(post.getOptions())
                .build();

        return Optional.ofNullable(redditTelegramForwarderBot.execute(sendPoll));
    }
}
