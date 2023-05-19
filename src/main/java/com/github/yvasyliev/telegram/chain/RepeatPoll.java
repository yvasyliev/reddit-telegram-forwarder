package com.github.yvasyliev.telegram.chain;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.telegram.TelegramSenderBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Properties;

public class RepeatPoll extends SubredditPostRepeaterChain {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepeatMultiplePhotos.class);

    @Autowired
    @Qualifier("appData")
    public Properties appData;

    public RepeatPoll(SubredditPostRepeaterChain nextChain) {
        super(nextChain);
    }

    @Override
    public void repeatRedditPost(JsonNode data, TelegramSenderBot telegramSenderBot, boolean needModerate) {
        if (data.has("poll_data")) {
            var redditOptions = data.get("poll_data").get("options").elements();
            var options = stream(redditOptions)
                    .map(redditOption -> redditOption.get("text").textValue())
                    .toList();

            try {
                telegramSenderBot.sendPoll(data.get("title").textValue(), options, needModerate);
                appData.setProperty("PREVIOUS_REDDIT_POST_CREATED", String.valueOf(data.get("created").intValue()));
            } catch (TelegramApiException e) {
                LOGGER.error(
                        "Failed to send multiple Photos. Created: {}, URL: {}",
                        data.get("created").intValue(),
                        data.get("url_overridden_by_dest").textValue(),
                        e
                );
            }
        } else {
            super.repeatRedditPost(data, telegramSenderBot, needModerate);
        }
    }
}
