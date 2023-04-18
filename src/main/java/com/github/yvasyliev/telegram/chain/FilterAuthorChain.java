package com.github.yvasyliev.telegram.chain;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.telegram.TelegramSenderBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;
import java.util.Set;

public class FilterAuthorChain extends SubredditPostRepeaterChain {
    @Value("#{'${SKIP_AUTHORS}'.split(',')}")
    private Set<String> restrictedAuthors;

    @Autowired
    @Qualifier("appData")
    public Properties appData;

    public FilterAuthorChain(SubredditPostRepeaterChain nextChain) {
        super(nextChain);
    }

    @Override
    public void repeatRedditPost(JsonNode data, TelegramSenderBot telegramSenderBot) {
        if (restrictedAuthors.contains(data.get("author").textValue())) {
            appData.setProperty("PREVIOUS_REDDIT_POST_CREATED", String.valueOf(data.get("created").intValue()));
        } else {
            super.repeatRedditPost(data, telegramSenderBot);
        }
    }
}
