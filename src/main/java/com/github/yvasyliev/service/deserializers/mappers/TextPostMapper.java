package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.model.dto.post.TextPost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@Order(1)
public class TextPostMapper implements PostMapper {
    @Value("#{{'.gifv'}}")
    private Set<String> videoExtensions;

    @Value("#{{'youtube.com', 'youtu.be'}}")
    private Set<String> youtubeDomains;

    @Value("""
            %s
                        
            %s""")
    private String postTextTemplate;

    @Override
    public Optional<Post> applyWithException(JsonNode jsonPost) {
        if (isTextPost(jsonPost)) {
            var text = postTextTemplate.formatted(
                    jsonPost.get("title").textValue(),
                    jsonPost.get("url_overridden_by_dest").textValue()
            );

            var post = new TextPost();
            post.setText(text);
            return Optional.of(post);
        }
        return Optional.empty();
    }

    private boolean isTextPost(JsonNode post) {
        if (post.has("post_hint")) {
            var postHint = post.get("post_hint").textValue();
            if ("link".equals(postHint)) {
                if (post.has("url_overridden_by_dest")) {
                    var urlOverriddenByDest = post.get("url_overridden_by_dest").textValue();
                    return videoExtensions.stream().noneMatch(urlOverriddenByDest::endsWith);
                }
                return true;
            }
        }
        return post.has("domain") && youtubeDomains.contains(post.get("domain").textValue());
    }
}
