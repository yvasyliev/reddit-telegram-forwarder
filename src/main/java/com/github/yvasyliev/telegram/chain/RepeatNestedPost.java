package com.github.yvasyliev.telegram.chain;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.telegram.TelegramSenderBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

import java.util.Properties;

public class RepeatNestedPost extends SubredditPostRepeaterChain {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("appData")
    public Properties appData;

    public RepeatNestedPost(SubredditPostRepeaterChain nextChain) {
        super(nextChain);
    }

    @Override
    public void repeatRedditPost(JsonNode data, TelegramSenderBot telegramSenderBot, boolean needModerate) {
        if (data.has("crosspost_parent_list")) {
            var created = appData.getProperty("PREVIOUS_REDDIT_POST_CREATED", "0");
            var subredditPostRepeaterChain = applicationContext.getBean("subredditPostRepeaterChain", SubredditPostRepeaterChain.class);
            subredditPostRepeaterChain.repeatRedditPost(
                    data.get("crosspost_parent_list").get(0),
                    telegramSenderBot,
                    needModerate
            );
            if (!created.equals(appData.getProperty("PREVIOUS_REDDIT_POST_CREATED"))) {
                appData.setProperty("PREVIOUS_REDDIT_POST_CREATED", String.valueOf(data.get("created").intValue()));
            }
        } else {
            super.repeatRedditPost(data, telegramSenderBot, needModerate);
        }
    }
}
