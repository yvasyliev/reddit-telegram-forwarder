package com.github.yvasyliev.telegram.chain;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.telegram.TelegramSenderBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RepeatNestedPost extends SubredditPostRepeaterChain {
    @Autowired
    @Qualifier("subredditPostRepeaterChain")
    private SubredditPostRepeaterChain subredditPostRepeaterChain;

    public RepeatNestedPost(SubredditPostRepeaterChain nextChain) {
        super(nextChain);
    }

    @Override
    public void repeatRedditPost(JsonNode data, TelegramSenderBot telegramSenderBot) {
        if (data.has("crosspost_parent_list")) {
            subredditPostRepeaterChain.repeatRedditPost(
                    data.get("crosspost_parent_list").get(0),
                    telegramSenderBot
            );
        } else {
            super.repeatRedditPost(data, telegramSenderBot);
        }
    }
}
