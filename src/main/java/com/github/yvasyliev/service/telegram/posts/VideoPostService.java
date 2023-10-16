package com.github.yvasyliev.service.telegram.posts;

import com.github.yvasyliev.model.dto.Post;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Service("VIDEO")
public class VideoPostService extends PostService<Message> {
    @Override
    public Optional<Message> applyWithException(String chatId, Post post) throws TelegramApiException {
        return Optional.ofNullable(sendWithInputStream((inputStream, filename) -> {
            var sendVideo = SendVideo.builder()
                    .chatId(chatId)
                    .video(new InputFile(inputStream, filename))
                    .caption(post.getText())
                    .hasSpoiler(post.isHasSpoiler())
                    .supportsStreaming(true)
                    .build();

            return redTelBot.execute(sendVideo);
        }, post));
    }
}
