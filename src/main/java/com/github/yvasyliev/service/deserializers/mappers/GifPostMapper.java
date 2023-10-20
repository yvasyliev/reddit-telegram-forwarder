package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.post.GifPost;
import com.github.yvasyliev.model.dto.post.Post;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Order(4)
public class GifPostMapper implements PostMapper {
    @Override
    public Optional<Post> applyWithException(JsonNode jsonPost) {
        if (isGif(jsonPost)) {
            var gifUrl = jsonPost
                    .get("preview")
                    .get("images")
                    .get(0)
                    .get("variants")
                    .get("mp4")
                    .get("source")
                    .get("url")
                    .textValue();
            var post = new GifPost();
            post.setText(jsonPost.get("title").textValue());
            post.setMediaUrl(gifUrl);
            post.setHasSpoiler("nsfw".equals(jsonPost.get("thumbnail").textValue()));
            return Optional.of(post);
        }
        return Optional.empty();
    }

    private boolean isGif(JsonNode data) {
        return data.has("url_overridden_by_dest")
                && data.get("url_overridden_by_dest").textValue().endsWith(".gif")
                && data.get("preview").get("images").get(0).get("variants").has("mp4");
    }
}