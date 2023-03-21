package com.github.yvasyliev.telegram;

import com.fasterxml.jackson.databind.JsonNode;
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
    public void repeatRedditPost(JsonNode data, TelegramRepeaterBot telegramRepeaterBot) {
        if (data.has("crosspost_parent_list")) {
            subredditPostRepeaterChain.repeatRedditPost(
                    data.get("crosspost_parent_list").get(0),
                    telegramRepeaterBot
            );
        } else {
            super.repeatRedditPost(data, telegramRepeaterBot);
        }
    }
}
