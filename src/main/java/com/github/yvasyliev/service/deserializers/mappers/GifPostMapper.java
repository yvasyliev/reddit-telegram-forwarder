package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.entity.Post;
import com.github.yvasyliev.model.dto.PostType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
public class GifPostMapper implements PostMapper {
    @Override
    public Post apply(JsonNode jsonPost) {
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
            var post = new Post();
            post.setType(PostType.GIF);
            post.setText(jsonPost.get("title").textValue());
            post.setMediaUrl(gifUrl);
            return post;
        }
        return null;
    }

    private boolean isGif(JsonNode data) {
        return data.has("url_overridden_by_dest")
                && data.get("url_overridden_by_dest").textValue().endsWith(".gif")
                && data.get("preview").get("images").get(0).get("variants").has("mp4");
    }
}
