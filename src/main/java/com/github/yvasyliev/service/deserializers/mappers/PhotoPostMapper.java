package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.post.PhotoPost;
import com.github.yvasyliev.model.dto.post.Post;
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
        return extractPhotoUrl(jsonPost)
                .map(photoUrl -> {
                    var text = jsonPost.get("title").textValue();
                    var post = new PhotoPost();
                    post.setText(text);
                    post.setMediaUrl(photoUrl);
                    post.setHasSpoiler("nsfw".equals(jsonPost.get("thumbnail").textValue()));
                    return post;
                });
    }

    private Optional<String> extractPhotoUrl(JsonNode post) {
        var urlOverriddenByDestNode = post.path("url_overridden_by_dest");
        if (!urlOverriddenByDestNode.isMissingNode()) {
            var urlOverriddenByDest = urlOverriddenByDestNode.textValue();
            if (urlOverriddenByDest.endsWith(".jpg1")) {
                return urlOverriddenByDest.substring(0, urlOverriddenByDest.length() - 1).describeConstable();
            }

            if (photoExtensions.stream().anyMatch(urlOverriddenByDest::endsWith)) {
                var photoUrl = post.at("/preview/images/0/source/url").asText();
                return (photoUrl.contains("auto=webp") ? urlOverriddenByDest : photoUrl).describeConstable();
            }
        }

        return Optional.empty();
    }
}
