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
public class TextPostMapper extends JsonNodeToPostConverter {
    @Value("#{{'.gifv'}}")
    private Set<String> videoExtensions;

    @Value("#{{'youtube.com', 'youtu.be'}}")
    private Set<String> youtubeDomains;

    @Value("""
            %s
                        
            %s""")
    private String postTextTemplate;

    @Override
    public Post convertThrowing(JsonNode jsonPost) {
        if (!isTextPost(jsonPost)) {
            return convertNext(jsonPost);
        }

        var text = postTextTemplate.formatted(
                title(jsonPost),
                jsonPost.get("url_overridden_by_dest").textValue()
        );

        var post = new TextPost();
        post.setText(text);
        return post;
    }

    private boolean isTextPost(JsonNode post) {
        return "link".equals(post.path("post_hint").asText())
                ? allowedVideoUrl(post)
                : youtubeDomains.contains(post.path("domain").asText());

    }

    private boolean allowedVideoUrl(JsonNode post) {
        return Optional
                .ofNullable(post.path("url_overridden_by_dest").textValue())
                .map(urlOverriddenByDest -> videoExtensions.stream().noneMatch(urlOverriddenByDest::endsWith))
                .orElse(true);
    }
}
