package com.github.yvasyliev.service.telegram.posts;

import com.github.yvasyliev.model.dto.Post;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingBiFunction;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Predicate;

@Service("PHOTO")
public class PhotoPostService extends PostService<Message> {
    @Override
    public Optional<Message> applyWithException(String chatId, Post post) throws TelegramApiException {
        try {
            var sendPhoto = SendPhoto.builder()
                    .chatId(chatId)
                    .photo(new InputFile(post.getMediaUrl()))
                    .caption(post.getText())
                    .hasSpoiler(post.isHasSpoiler())
                    .build();
            return Optional.ofNullable(redTelBot.execute(sendPhoto));
        } catch (TelegramApiRequestException e) {
            return Optional.ofNullable(retrySendPhoto(chatId, post, e));
        }
    }

    private Message retrySendPhoto(String chatId, Post post, TelegramApiRequestException requestException) throws TelegramApiException {
        try {
            return onException(
                    requestException,
                    ex -> "Bad Request: wrong file identifier/HTTP URL specified".equals(ex.getApiResponse()),
                    (inputStream, filename) -> {
                        var sendPhoto = SendPhoto.builder()
                                .chatId(chatId)
                                .photo(new InputFile(inputStream, filename))
                                .caption(post.getText())
                                .hasSpoiler(post.isHasSpoiler())
                                .build();
                        return redTelBot.execute(sendPhoto);
                    },
                    post
            );
        } catch (TelegramApiRequestException e) {
            return onException(
                    requestException,
                    ex -> "Bad Request: PHOTO_INVALID_DIMENSIONS".equals(ex.getApiResponse()),
                    (inputStream, filename) -> {
                        var sendPhoto = SendDocument.builder()
                                .chatId(chatId)
                                .document(new InputFile(inputStream, filename))
                                .caption(post.getText())
                                .build();
                        return redTelBot.execute(sendPhoto);
                    },
                    post
            );
        }
    }

    private Message onException(
            TelegramApiRequestException e,
            Predicate<TelegramApiRequestException> exceptionPredicate,
            ThrowingBiFunction<InputStream, String, Message> telegramApiCall,
            Post post
    ) throws TelegramApiException {
        if (exceptionPredicate.negate().test(e)) {
            throw e;
        }

        return sendWithInputStream(telegramApiCall, post);
    }
}
