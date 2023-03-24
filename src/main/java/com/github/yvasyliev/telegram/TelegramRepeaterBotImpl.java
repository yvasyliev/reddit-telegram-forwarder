package com.github.yvasyliev.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.telegram.chain.SubredditPostRepeaterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class TelegramRepeaterBotImpl extends AbstractTelegramBot implements TelegramRepeaterBot {
    @Autowired
    @Qualifier("subredditPostRepeaterChain")
    private SubredditPostRepeaterChain subredditPostRepeaterChain;

    @Autowired
    private TelegramSenderBot telegramSenderBot;

    public TelegramRepeaterBotImpl(String botToken) {
        super(botToken);
    }

    @Override
    public void repeatSubredditPosts(List<JsonNode> dataStream) {
        dataStream.forEach(data -> subredditPostRepeaterChain.repeatRedditPost(data, telegramSenderBot));
    }
}
