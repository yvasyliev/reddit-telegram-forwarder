package com.github.yvasyliev.telegram;

import com.fasterxml.jackson.databind.JsonNode;
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

    @Override
    public void repeatRedditPost(JsonNode data, TelegramRepeaterBot telegramRepeaterBot) {
        var photoUrl = extractPhotoUrl(data);
        if (photoUrl != null) {
            var filename = photoUrl.substring(photoUrl.lastIndexOf('/') + 1);
            var text = data.get("title").textValue();
            var hasSpoiler = hasSpoiler(data);

            try {
                try {
                    send(photoUrl, inputStream -> telegramRepeaterBot.sendPhoto(
                            inputStream,
                            filename,
                            text,
                            hasSpoiler
                    ));
                } catch (TelegramApiRequestException e) {
                    var apiResponse = e.getApiResponse();

                    if (!apiResponse.contains("PHOTO_INVALID_DIMENSIONS") && !apiResponse.endsWith("too big for a photo")) {
                        throw e;
                    }

                    send(photoUrl, inputStream -> telegramRepeaterBot.sendDocument(
                            inputStream,
                            filename,
                            text
                    ));
                }
                appData.setProperty("created", data.get("created").asText());
            } catch (IOException | TelegramApiException e) {
                LOGGER.error(
                        "Failed to send photo. Created: {}, URL: {}",
                        data.get("created").intValue(),
                        data.get("url_overridden_by_dest").textValue(),
                        e
                );
            }
        } else {
            super.repeatRedditPost(data, telegramRepeaterBot);
        }
    }

    private String extractPhotoUrl(JsonNode data) {
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

        return null;
    }

    private void send(String fileUrl, SendFileTask sendFileTask) throws IOException, TelegramApiException {
        try (var inputStream = new URL(fileUrl).openStream()) {
            sendFileTask.doTask(inputStream);
        }
    }

    @FunctionalInterface
    private interface SendFileTask {
        void doTask(InputStream file) throws TelegramApiException;
    }
}
