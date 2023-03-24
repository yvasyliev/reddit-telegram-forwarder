package com.github.yvasyliev.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.List;

public class TelegramSenderBotImpl extends AbstractTelegramBot implements TelegramSenderBot {
    @Value("${CHANNEL_ID}")
    private String channelId;

    public TelegramSenderBotImpl(String botToken) {
        super(botToken);
    }

    @Override
    public void sendText(String text) throws TelegramApiException {
        var sendMessage = new SendMessage(channelId, text);
        execute(sendMessage);
    }

    @Override
    public void sendPhoto(String photoUrl, String text, boolean hasSpoiler) throws TelegramApiException {
        var sendPhoto = SendPhoto.builder()
                .chatId(channelId)
                .photo(new InputFile(photoUrl))
                .caption(text)
                .hasSpoiler(hasSpoiler)
                .build();

        execute(sendPhoto);
    }

    @Override
    public void sendMultiplePhotos(List<String> photoUrls, String text, boolean hasSpoiler) throws TelegramApiException {
        var inputMediaPhotos = photoUrls
                .stream()
                .map(photoUrl -> (InputMedia) InputMediaPhoto
                        .builder()
                        .media(photoUrl)
                        .hasSpoiler(hasSpoiler)
                        .build()
                )
                .toList();

        inputMediaPhotos.get(0).setCaption(text);

        var sendMediaGroup = SendMediaGroup.builder()
                .chatId(channelId)
                .medias(inputMediaPhotos)
                .build();

        execute(sendMediaGroup);
    }

    @Override
    public void sendGif(InputStream gif, String filename, String text, boolean hasSpoiler) throws TelegramApiException {
        var sendAnimation = SendAnimation.builder()
                .chatId(channelId)
                .animation(new InputFile(gif, filename))
                .caption(text)
                .hasSpoiler(hasSpoiler)
                .build();

        execute(sendAnimation);
    }

    @Override
    public void sendVideo(InputStream video, String filename, String text, boolean hasSpoiler) throws TelegramApiException {
        var sendVideo = SendVideo.builder()
                .chatId(channelId)
                .video(new InputFile(video, filename))
                .caption(text)
                .hasSpoiler(hasSpoiler)
                .supportsStreaming(true)
                .build();

        execute(sendVideo);
    }

    @Override
    public void sendDocument(InputStream document, String filename, String text) throws TelegramApiException {
        SendDocument sendDocument = SendDocument.builder()
                .chatId(channelId)
                .document(new InputFile(document, filename))
                .caption(text)
                .build();

        execute(sendDocument);
    }
}
