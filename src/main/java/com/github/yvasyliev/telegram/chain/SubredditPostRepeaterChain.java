package com.github.yvasyliev.telegram.chain;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.telegram.TelegramSenderBot;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    protected Stream<JsonNode> stream(Iterator<JsonNode> elements) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(elements, Spliterator.ORDERED), false);
    }
}
