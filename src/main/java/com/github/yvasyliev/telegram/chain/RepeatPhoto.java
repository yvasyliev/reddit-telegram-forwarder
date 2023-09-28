package com.github.yvasyliev.telegram.chain;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.telegram.TelegramSenderBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

public class RepeatPhoto extends SubredditPostRepeaterChain {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepeatPhoto.class);
    @Value("#{{'.jpg', '.png', '.jpeg'}}")
    private Set<String> photoExtensions;

    @Autowired
    @Qualifier("appData")
    public Properties appData;

    public RepeatPhoto(SubredditPostRepeaterChain nextChain) {
        super(nextChain);
    }

    // TODO: 9/28/2023 refactor it.
    @Override
    public void repeatRedditPost(JsonNode data, TelegramSenderBot telegramSenderBot, boolean needModerate) {
        var photoUrl = extractPhotoUrl(data);
        if (photoUrl != null) {
            var text = data.get("title").textValue();
            var hasSpoiler = hasSpoiler(data);

            try {
                try {
                    telegramSenderBot.sendPhoto(photoUrl, text, hasSpoiler, needModerate);
                } catch (TelegramApiRequestException e) {
                    var apiResponse = e.getApiResponse();

                    if ("Bad Request: wrong file identifier/HTTP URL specified".equals(apiResponse)) {
                        try {
                            sendPhotoInputStream(
                                    photoUrl,
                                    (inputStream, filename) -> telegramSenderBot.sendPhoto(inputStream, filename, text, hasSpoiler, needModerate)
                            );
                            appData.setProperty("PREVIOUS_REDDIT_POST_CREATED", String.valueOf(data.get("created").intValue()));
                            return;
                        } catch (TelegramApiRequestException ex) {
                            apiResponse = ex.getApiResponse();
                        }
                    }

                    if ("Bad Request: PHOTO_INVALID_DIMENSIONS".equals(apiResponse)) {
                        sendPhotoInputStream(
                                photoUrl,
                                (inputStream, filename) -> telegramSenderBot.sendDocument(inputStream, filename, text, needModerate)
                        );
                    } else {
                        throw e;
                    }
                }
                appData.setProperty("PREVIOUS_REDDIT_POST_CREATED", String.valueOf(data.get("created").intValue()));
            } catch (IOException | TelegramApiException e) {
                LOGGER.error(
                        "Failed to send photo. Created: {}, URL: {}",
                        data.get("created").intValue(),
                        data.get("url_overridden_by_dest").textValue(),
                        e
                );
            }
        } else {
            super.repeatRedditPost(data, telegramSenderBot, needModerate);
        }
    }

    private String extractPhotoUrl(JsonNode data) {
        if (data.has("url_overridden_by_dest")) {
            var urlOverriddenByDest = data.get("url_overridden_by_dest").textValue();

            if (urlOverriddenByDest.endsWith(".jpg1")) {
                return urlOverriddenByDest.substring(0, urlOverriddenByDest.length() - 1);
            }

            if (photoExtensions.stream().anyMatch(urlOverriddenByDest::endsWith)) {
                var photoUrl = data
                        .get("preview")
                        .get("images")
                        .get(0)
                        .get("source")
                        .get("url")
                        .textValue();

                return photoUrl.contains("auto=webp") ? urlOverriddenByDest : photoUrl;
            }
        }

        return null;
    }

    private void sendPhotoInputStream(String photoUrl, SenderFunction senderFunction) throws IOException, TelegramApiException {
        try (var inputStream = new URL(photoUrl).openStream()) {
            var filename = photoUrl.substring(photoUrl.lastIndexOf('/') + 1);
            senderFunction.send(inputStream, filename);
        }
    }

    @FunctionalInterface
    private interface SenderFunction {
        void send(InputStream inputStream, String filename) throws TelegramApiException;
    }
}
