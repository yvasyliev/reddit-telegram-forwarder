package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.post.PollPost;
import com.github.yvasyliev.model.dto.post.Post;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Order(6)
public class PollPostMapper implements PostMapper {
    @Override
    public Optional<Post> applyWithException(JsonNode jsonPost) {
        if (jsonPost.has("poll_data")) {
            var redditOptions = jsonPost.get("poll_data").get("options").elements();
            var options = stream(redditOptions)
                    .map(redditOption -> redditOption.get("text").textValue())
                    .toList();
            var post = new PollPost();
            post.setText(jsonPost.get("title").textValue());
            post.setOptions(options);
            return Optional.of(post);
        }
        return Optional.empty();
    }
}
