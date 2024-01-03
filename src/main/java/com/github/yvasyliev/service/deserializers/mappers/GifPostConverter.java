package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.post.GifPost;
import com.github.yvasyliev.model.dto.post.Post;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
public class GifPostConverter extends JsonNodeToPostConverter {
    @Override
    public Post convertThrowing(JsonNode jsonPost) {
        var url = jsonPost.at("/preview/images/0/variants/mp4/source/url");

        if (url.isMissingNode()) {
            return convertNext(jsonPost);
        }

        var post = new GifPost();
        post.setText(title(jsonPost));
        post.setMediaUrl(url.textValue());
        post.setHasSpoiler(nsfw(jsonPost));
        return post;
    }
}
