package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.post.GifPost;
import com.github.yvasyliev.model.dto.post.Post;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Order(4)
public class GifPostMapper implements PostMapper {
    @Override
    @NonNull
    public Optional<Post> applyWithException(@NonNull JsonNode jsonPost) {
        var url = jsonPost.at("/preview/images/0/variants/mp4/source/url");

        if (url.isMissingNode()) {
            return Optional.empty();
        }

        var gifUrl = url.textValue();
        var post = new GifPost();
        post.setText(jsonPost.get("title").textValue());
        post.setMediaUrl(gifUrl);
        post.setHasSpoiler("nsfw".equals(jsonPost.get("thumbnail").textValue()));
        return Optional.of(post);
    }
}
