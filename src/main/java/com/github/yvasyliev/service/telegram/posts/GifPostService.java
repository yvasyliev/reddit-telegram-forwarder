package com.github.yvasyliev.service.telegram.posts;

import com.github.yvasyliev.model.dto.post.GifPost;
import com.github.yvasyliev.model.dto.post.Post;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.URISyntaxException;
import java.util.Optional;

@Service(Post.Type.GIF)
public class GifPostService extends PostService<GifPost, Message> {
    @Override
    @NonNull
    public Optional<Message> applyWithException(@NonNull String chatId, @NonNull GifPost post) throws TelegramApiException {
        return Optional.ofNullable(sendWithInputStream((inputStream, filename) -> {
            var sendAnimation = SendAnimation.builder()
                    .chatId(chatId)
                    .animation(new InputFile(inputStream, filename))
                    .caption(post.getText())
                    .hasSpoiler(post.isHasSpoiler())
                    .build();

            return redditTelegramForwarderBot.execute(sendAnimation);
        }, post));
    }

    @Override
    protected String parseFilename(String mediaUrl) throws URISyntaxException {
        return super.parseFilename(mediaUrl).replace(".gif", ".mp4");
    }
}
