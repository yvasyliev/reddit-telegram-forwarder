package com.github.yvasyliev.service.reddit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.service.state.StateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingSupplier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

@Service
public class SubredditPostSupplier implements ThrowingSupplier<List<Post>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubredditPostSupplier.class);

    @Autowired
    private ThrowingSupplier<JsonNode> subredditNewSupplier;

    @Value("30")
    private int delayMinutes;

    @Autowired
    private StateManager stateManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @NonNull
    public List<Post> getWithException() throws Exception {
        var subredditPosts = subredditNewSupplier.getWithException();
        var children = subredditPosts
                .get("data")
                .get("children")
                .elements();
        var lastCreated = stateManager.getLastCreated();
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(children, Spliterator.ORDERED), false)
                .map(child -> child.get("data"))
                .filter(data -> {
                    var created = data.get("created").intValue();
                    var createdUtc = Instant.ofEpochMilli(created * 1000L);
                    var deadline = Instant.now().minus(delayMinutes, ChronoUnit.MINUTES);
                    return created > lastCreated
                            && createdUtc.isBefore(deadline);
                })
                .map(data -> {
                    try {
                        return objectMapper.treeToValue(data, Post.class);
                    } catch (JsonProcessingException e) {
                        LOGGER.error(
                                "Failed to deserialize post. Created: {}, URL: {}",
                                data.get("created").intValue(),
                                data.get("url").textValue(),
                                e
                        );
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted()
                .toList();
    }
}
