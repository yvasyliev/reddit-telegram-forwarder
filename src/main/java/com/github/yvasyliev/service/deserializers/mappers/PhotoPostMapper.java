package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.Post;
import com.github.yvasyliev.model.dto.PostType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@Order(2)
public class PhotoPostMapper implements PostMapper {
    @Value("#{{'.jpg', '.png', '.jpeg'}}")
    private Set<String> photoExtensions;

    @Override
    public Optional<Post> applyWithException(JsonNode jsonPost) {
        var photoUrl = extractPhotoUrl(jsonPost);
        if (photoUrl != null) {
            var text = jsonPost.get("title").textValue();
            var post = new Post();
            post.setType(PostType.PHOTO);
            post.setText(text);
            post.setMediaUrl(photoUrl);
            return Optional.of(post);
        }
        return Optional.empty();
    }

    private String extractPhotoUrl(JsonNode post) {
        if (post.has("url_overridden_by_dest")) {
            var urlOverriddenByDest = post.get("url_overridden_by_dest").textValue();

            if (urlOverriddenByDest.endsWith(".jpg1")) {
                return urlOverriddenByDest.substring(0, urlOverriddenByDest.length() - 1);
            }

            if (photoExtensions.stream().anyMatch(urlOverriddenByDest::endsWith)) {
                var photoUrl = post
                        .get("preview")
                        .get("images")
                        .get(0)
                        .get("source")
                        .get("url")
                        .textValue();

                return photoUrl.contains("auto=webp") ? urlOverriddenByDest : photoUrl;
            }
        }

        return null;
    }
}
