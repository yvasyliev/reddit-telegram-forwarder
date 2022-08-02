package app.funclub.anadearmas.telegram;

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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class AnadeArmasFanbot extends TelegramLongPollingBot {
    private static final long CHANNEL_ID = -1001683714987L;
    private static final long DEVELOPER_ID = 390649240;

    public void sendGif(String gifUrl, String text) throws TelegramApiException {
        SendAnimation sendAnimation = SendAnimation.builder()
                .chatId(String.valueOf(CHANNEL_ID))
                .animation(new InputFile(gifUrl))
                .caption(text)
                .build();

        execute(sendAnimation);
    }

    public void sendMultiplePhotos(List<String> photoUrls, String text) throws TelegramApiException {
        List<InputMedia> inputMedia = photoUrls.stream()
                .map(InputMediaPhoto::new)
                .collect(Collectors.toList());

        inputMedia.get(0).setCaption(text);

        SendMediaGroup sendMediaGroup = SendMediaGroup.builder()
                .chatId(String.valueOf(CHANNEL_ID))
                .medias(inputMedia)
                .build();

        execute(sendMediaGroup);
    }

    public void sendVideo(String videoUrl, String text) throws TelegramApiException, IOException {
        try (InputStream inputStream = new URL(videoUrl).openStream()) {
            String fileName = videoUrl.substring(videoUrl.lastIndexOf('/') + 1);
            SendVideo sendVideo = SendVideo.builder()
                    .chatId(String.valueOf(CHANNEL_ID))
                    .video(new InputFile(inputStream, fileName))
                    .caption(text)
                    .build();

            execute(sendVideo);
        }
    }

    public void sendText(String text) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(String.valueOf(CHANNEL_ID), text);
        execute(sendMessage);
    }

    public void sendPhoto(String photoUrl, String text) throws TelegramApiException, IOException {
        try (InputStream inputStream = new URL(photoUrl).openStream()) {
            String fileName = photoUrl.substring(photoUrl.lastIndexOf('/') + 1);
            SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(String.valueOf(CHANNEL_ID))
                    .photo(new InputFile(inputStream, fileName))
                    .caption(text)
                    .build();

            execute(sendPhoto);
        }
    }

    public void sendDocument(String docUrl, String text) throws TelegramApiException, IOException {
        try (InputStream inputStream = new URL(docUrl).openStream()) {
            String fileName = docUrl.substring(docUrl.lastIndexOf('/') + 1);
            SendDocument sendDocument = SendDocument.builder()
                    .chatId(String.valueOf(CHANNEL_ID))
                    .document(new InputFile(inputStream, fileName))
                    .caption(text)
                    .build();

            execute(sendDocument);
        }
    }

    public void sendDeveloperMessage(String text) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(String.valueOf(DEVELOPER_ID))
                .text("```" + text + "```")
                .parseMode(ParseMode.MARKDOWN)
                .build();
        execute(sendMessage);
    }

    @Override
    public String getBotUsername() {
        return "Ana_de_Armas_fanbot";
    }

    @Override
    public String getBotToken() {
        return "5447965600:AAEqVNRqOixVdxORFWQgaSywzCfRo6-tBus";
    }

    @Override
    public void onUpdateReceived(Update update) {
    }
}
