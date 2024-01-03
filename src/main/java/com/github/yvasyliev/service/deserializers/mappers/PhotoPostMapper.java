package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.post.PhotoPost;
import com.github.yvasyliev.model.dto.post.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Order(2)
public class PhotoPostMapper extends JsonNodeToPostConverter {
    @Value("#{{'.jpg', '.png', '.jpeg'}}")
    private Set<String> photoExtensions;

    @Override
    public Post convertThrowing(JsonNode jsonPost) {
        var photoUrl = extractPhotoUrl(jsonPost);

        if (photoUrl == null) {
            return convertNext(jsonPost);
        }

        var post = new PhotoPost();
        post.setText(title(jsonPost));
        post.setMediaUrl(photoUrl);
        post.setHasSpoiler(nsfw(jsonPost));
        return post;
    }

    private String extractPhotoUrl(JsonNode post) {
        var urlOverriddenByDestNode = post.path("url_overridden_by_dest");
        if (!urlOverriddenByDestNode.isMissingNode()) {
            var urlOverriddenByDest = urlOverriddenByDestNode.textValue();
            if (urlOverriddenByDest.endsWith(".jpg1")) {
                return urlOverriddenByDest.substring(0, urlOverriddenByDest.length() - 1);
            }

            if (photoExtensions.stream().anyMatch(urlOverriddenByDest::endsWith)) {
                var photoUrl = post.at("/preview/images/0/source/url").asText();
                return (photoUrl.contains("auto=webp") ? urlOverriddenByDest : photoUrl);
            }
        }

        return null;
    }
}
