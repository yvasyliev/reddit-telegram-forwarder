package com.github.yvasyliev.telegram.chain;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.telegram.TelegramSenderBot;

public class NsfwCheckChain extends SubredditPostRepeaterChain {
    public NsfwCheckChain(SubredditPostRepeaterChain nextChain) {
        super(nextChain);
    }

    @Override
    public void repeatRedditPost(JsonNode data, TelegramSenderBot telegramSenderBot, boolean needModerate) {
        super.repeatRedditPost(data, telegramSenderBot, hasSpoiler(data));
    }
}
