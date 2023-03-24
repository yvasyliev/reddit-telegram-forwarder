package com.github.yvasyliev.telegram;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

@FunctionalInterface
public interface TelegramRepeaterBot {
    void repeatSubredditPosts(List<JsonNode> dataList);
}
