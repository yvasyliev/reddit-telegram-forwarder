package com.github.yvasyliev.service.telegram.posts;

import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.model.dto.post.VideoPost;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Service(Post.Type.VIDEO)
public class VideoPostService extends PostService<VideoPost, Message> {
    @Override
    @NonNull
    public Optional<Message> applyWithException(@NonNull String chatId, @NonNull VideoPost post) throws TelegramApiException {
        return Optional.ofNullable(sendWithInputStream((inputStream, filename) -> {
            var sendVideo = SendVideo.builder()
                    .chatId(chatId)
                    .video(new InputFile(inputStream, filename))
                    .caption(post.getText())
                    .hasSpoiler(post.isHasSpoiler())
                    .supportsStreaming(true)
                    .build();

            return redditTelegramForwarderBot.execute(sendVideo);
        }, post));
    }
}
