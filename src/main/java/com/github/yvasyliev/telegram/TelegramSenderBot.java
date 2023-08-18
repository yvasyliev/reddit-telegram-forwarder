package com.github.yvasyliev.telegram;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

public interface TelegramSenderBot {
    void sendText(String text, boolean needModerate) throws TelegramApiException;

    void sendPhoto(String photoUrl, String text, boolean hasSpoiler, boolean needModerate) throws TelegramApiException;

    void sendPhoto(InputStream photo, String filename, String text, boolean hasSpoiler, boolean needModerate) throws TelegramApiException;

    void sendMultiplePhotos(List<String> photoUrls, String text, boolean hasSpoiler, boolean needModerate) throws TelegramApiException;

    void sendGif(InputStream gif, String filename, String text, boolean hasSpoiler, boolean needModerate) throws TelegramApiException;

    void sendVideo(InputStream video, String filename, String text, boolean hasSpoiler, boolean needModerate) throws TelegramApiException;

    void sendDocument(InputStream document, String filename, String text, boolean needModerate) throws TelegramApiException;

    void sendPoll(String question, Collection<String> options, boolean needModerate) throws TelegramApiException;
}
