package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.Post;
import com.github.yvasyliev.model.dto.PostType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(6)
public class PollPostMapper implements PostMapper {
    @Override
    public Post apply(JsonNode jsonPost) {
        if (jsonPost.has("poll_data")) {
            var redditOptions = jsonPost.get("poll_data").get("options").elements();
            var options = stream(redditOptions)
                    .map(redditOption -> redditOption.get("text").textValue())
                    .toList();
            var post = new Post();
            post.setType(PostType.POLL);
            post.setText(jsonPost.get("title").textValue());
            post.setOptions(options);
            return post;
        }
        return null;
    }
}
