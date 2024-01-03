package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.post.PollPost;
import com.github.yvasyliev.model.dto.post.Post;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(6)
public class PollPostMapper extends JsonNodeToPostConverter {
    @Override
    public Post convertThrowing(JsonNode jsonPost) {
        var redditOptions = jsonPost.at("/poll_data/options");

        if (redditOptions.isMissingNode()) {
            return convertNext(jsonPost);
        }

        var options = stream(redditOptions.elements())
                .map(redditOption -> redditOption.get("text").textValue())
                .toList();
        var post = new PollPost();
        post.setText(title(jsonPost));
        post.setOptions(options);
        return post;
    }
}
