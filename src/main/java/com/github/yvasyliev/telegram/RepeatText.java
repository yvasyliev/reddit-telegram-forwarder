package com.github.yvasyliev.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Properties;
import java.util.Set;

public class RepeatText extends SubredditPostRepeaterChain {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepeatText.class);

    @Value("#{{'youtube.com', 'youtu.be'}}")
    private Set<String> youtubeDomains;

    @Autowired
    @Qualifier("appData")
    public Properties appData;

    public RepeatText(SubredditPostRepeaterChain nextChain) {
        super(nextChain);
    }

    @Override
    public void repeatRedditPost(JsonNode data, TelegramRepeaterBot telegramRepeaterBot) {
        if ("link".equals(data.get("post_hint").textValue()) || youtubeDomains.contains(data.get("domain").textValue())) {
            try {
                telegramRepeaterBot.sendText(data.get("title").textValue() + "\n" + data.get("url_overridden_by_dest").textValue());
                appData.setProperty("created", data.get("created").asText());
            } catch (TelegramApiException e) {
                LOGGER.error(
                        "Failed to send text. Created: {}, URL: {}",
                        data.get("created").intValue(),
                        data.get("url_overridden_by_dest").textValue(),
                        e
                );
            }
        } else {
            super.repeatRedditPost(data, telegramRepeaterBot);
        }
    }
}
