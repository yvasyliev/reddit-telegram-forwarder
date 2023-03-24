package com.github.yvasyliev.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.Set;

public class NOPRepeaterChain extends SubredditPostRepeaterChain {
    private static final Logger LOGGER = LoggerFactory.getLogger(NOPRepeaterChain.class);

    @Value("#{{'vimeo.com'}}")
    private Set<String> ignoredDomains;

    public NOPRepeaterChain() {
        super(null);
    }

    @Override
    public void repeatRedditPost(JsonNode data, TelegramSenderBot telegramSenderBot) {
        if (!ignoredDomains.contains(data.get("domain").textValue())) {
            LOGGER.error(
                    "Post was not repeated. Created: {}, URL: {}",
                    data.get("created").intValue(),
                    data.get("url_overridden_by_dest").textValue()
            );
        }
    }
}
