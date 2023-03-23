package com.github.yvasyliev.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class TelegramRepeaterBot extends TelegramLongPollingBot {
    @Autowired
    @Qualifier("subredditPostRepeaterChain")
    private SubredditPostRepeaterChain subredditPostRepeaterChain;

    @Value("${CHANNEL_ID}")
    private String channelId;

    @Value("${DEVELOPER_ID}")
    private String developerId;

    @Value("${BOT_USERNAME}")
    private String botUsername;

    @Value("""
            ```
            {0}
            ```""")
    private String developerTextTemplate;

    public TelegramRepeaterBot(String botToken) {
        super(botToken);
    }

    public void repeatSubredditPosts(List<JsonNode> dataStream) {
        dataStream.forEach(data -> subredditPostRepeaterChain.repeatRedditPost(data, this));
    }

    public void sendText(String text) throws TelegramApiException {
        var sendMessage = new SendMessage(channelId, text);
        execute(sendMessage);
    }

    public void sendPhoto(InputStream photo, String filename, String text, boolean hasSpoiler) throws TelegramApiException {
        var sendPhoto = SendPhoto.builder()
                .chatId(channelId)
                .photo(new InputFile(photo, filename))
                .caption(text)
                .hasSpoiler(hasSpoiler)
                .build();

        execute(sendPhoto);
    }

    public void sendMultiplePhotos(Map<String, InputStream> photos, String text, boolean hasSpoiler) throws TelegramApiException {
        var inputMediaPhotos = photos
                .entrySet()
                .stream()
                .map(photo -> (InputMedia) InputMediaPhoto
                        .builder()
                        .mediaName(photo.getKey())
                        .newMediaStream(photo.getValue())
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

    public void sendGif(InputStream gif, String filename, String text, boolean hasSpoiler) throws TelegramApiException {
        var sendAnimation = SendAnimation.builder()
                .chatId(channelId)
                .animation(new InputFile(gif, filename))
                .caption(text)
                .hasSpoiler(hasSpoiler)
                .build();

        execute(sendAnimation);
    }

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

    public void sendDocument(InputStream document, String filename, String text) throws TelegramApiException {
        SendDocument sendDocument = SendDocument.builder()
                .chatId(channelId)
                .document(new InputFile(document, filename))
                .caption(text)
                .build();

        execute(sendDocument);
    }

    public void sendDeveloperMessage(String text) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(developerId)
                .text(MessageFormat.format(developerTextTemplate, text))
                .parseMode(ParseMode.MARKDOWN)
                .build();
        execute(sendMessage);
    }

    @Override
    public void onUpdateReceived(Update update) {
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
