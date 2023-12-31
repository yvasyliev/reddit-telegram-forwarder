package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.model.dto.post.TextPost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
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
    @NonNull
    public Optional<Post> applyWithException(@NonNull JsonNode jsonPost) {
        if (!isTextPost(jsonPost)) {
            return Optional.empty();
        }

        var text = postTextTemplate.formatted(
                jsonPost.get("title").textValue(),
                jsonPost.get("url_overridden_by_dest").textValue()
        );

        var post = new TextPost();
        post.setText(text);
        return Optional.of(post);
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
