package com.github.yvasyliev.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class RepeatGif extends SubredditPostRepeaterChain {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepeatGif.class);

    @Autowired
    @Qualifier("appData")
    public Properties appData;

    public RepeatGif(SubredditPostRepeaterChain nextChain) {
        super(nextChain);
    }

    @Override
    public void repeatRedditPost(JsonNode data, TelegramRepeaterBot telegramRepeaterBot) {
        if (isGif(data)) {
            var gifUrl = data
                    .get("preview")
                    .get("images")
                    .get(0)
                    .get("variants")
                    .get("mp4")
                    .get("source")
                    .get("url")
                    .textValue();

            var fileName = gifUrl.substring(gifUrl.lastIndexOf('/') + 1);
            if (fileName.contains("?")) {
                fileName = fileName.substring(0, fileName.indexOf("?"));
            }

            try (var inputStream = new URL(gifUrl).openStream()) {
                telegramRepeaterBot.sendGif(
                        inputStream,
                        fileName,
                        data.get("title").textValue(),
                        hasSpoiler(data)
                );
                appData.setProperty("created", data.get("created").asText());
            } catch (IOException | TelegramApiException e) {
                LOGGER.error(
                        "Failed to send gif. Created: {}, URL: {}",
                        data.get("created").intValue(),
                        data.get("url_overridden_by_dest").textValue(),
                        e
                );
            }
        } else {
            super.repeatRedditPost(data, telegramRepeaterBot);
        }
    }

    private boolean isGif(JsonNode data) {
        return data.get("url_overridden_by_dest").textValue().endsWith(".gif")
                && data.get("preview").get("images").get(0).get("variants").has("mp4");
    }
}
