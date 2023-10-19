package com.github.yvasyliev.service.telegram.posts;

import com.github.yvasyliev.bots.telegram.RedTelBot;
import com.github.yvasyliev.model.dto.post.MediaPost;
import com.github.yvasyliev.model.dto.post.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.function.ThrowingBiFunction;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

public abstract class PostService<U extends Post, R> implements ThrowingBiFunction<String, U, Optional<R>> {
    @Autowired
    protected RedTelBot redTelBot;

    protected Message sendWithInputStream(ThrowingBiFunction<InputStream, String, Message> telegramApiCall, MediaPost post) throws TelegramApiException {
        var mediaUrl = post.getMediaUrl();
        try (var inputStream = new URL(mediaUrl).openStream()) {
            var filename = mediaUrl.substring(mediaUrl.lastIndexOf('/') + 1);
            if (filename.contains("?")) {
                filename = filename.substring(0, filename.indexOf("?"));
            }
            return telegramApiCall.applyWithException(inputStream, filename);
        } catch (Exception e) {
            throw new TelegramApiException(e);
        }
    }
}
