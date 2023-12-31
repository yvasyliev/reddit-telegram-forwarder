package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.post.PollPost;
import com.github.yvasyliev.model.dto.post.Post;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Order(6)
public class PollPostMapper implements PostMapper {
    @Override
    @NonNull
    public Optional<Post> applyWithException(@NonNull JsonNode jsonPost) {
        return Optional
                .ofNullable(jsonPost.path("poll_data").get("options"))
                .map(redditOptions -> {
                    var options = stream(redditOptions.elements())
                            .map(redditOption -> redditOption.get("text").textValue())
                            .toList();
                    var post = new PollPost();
                    post.setText(jsonPost.get("title").textValue());
                    post.setOptions(options);
                    return post;
                });
    }
}
