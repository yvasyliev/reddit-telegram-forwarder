package com.github.yvasyliev.telegram;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class SubredditPostRepeaterChain {
    private final SubredditPostRepeaterChain nextChain;

    public SubredditPostRepeaterChain(SubredditPostRepeaterChain nextChain) {
        this.nextChain = nextChain;
    }

    public void repeatRedditPost(JsonNode data, TelegramSenderBot telegramSenderBot) {
        nextChain.repeatRedditPost(data, telegramSenderBot);
    }

    protected boolean hasSpoiler(JsonNode data) {
        return "nsfw".equals(data.get("thumbnail").textValue());
    }
}
