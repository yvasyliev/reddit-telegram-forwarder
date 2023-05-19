package com.github.yvasyliev.telegram.chain;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.telegram.TelegramSenderBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.Set;

public class NOPRepeaterChain extends SubredditPostRepeaterChain {
    private static final Logger LOGGER = LoggerFactory.getLogger(NOPRepeaterChain.class);

    @Value("#{{'vimeo.com', 'gfycat.com'}}")
    private Set<String> ignoredDomains;

    @Value("""
            Post was not repeated.
            Created: {}
            URL: {}""")
    private String postNotHandledMessageTemplate;

    public NOPRepeaterChain() {
        super(null);
    }

    @Override
    public void repeatRedditPost(JsonNode data, TelegramSenderBot telegramSenderBot, boolean needModerate) {
        if (!ignoredDomains.contains(data.get("domain").textValue())) {
            LOGGER.error(
                    postNotHandledMessageTemplate,
                    data.get("created").intValue(),
                    data.get("url").textValue()
            );
        }
    }
}
